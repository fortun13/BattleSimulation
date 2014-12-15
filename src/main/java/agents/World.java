package main.java.agents;

import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeyMissingException;
import edu.wlu.cs.levy.CG.KeySizeException;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;
import javafx.geometry.Point2D;
import javafx.util.Pair;
import main.java.gui.OptionsPanel;
import main.java.gui.SideOptionPanel;
import main.java.utils.*;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Marek on 2014-11-15.
 * Gives possibility for agents to figure out where are they
 */
public class World {

    private static int offset = 0;
    private final KDTree<AgentInTree> agentsTree = new KDTree<>(2);
    public ArrayList<AID> bluesAgents = new ArrayList<>();
    public ArrayList<AID> redsAgents = new ArrayList<>();
    public ServerAgent server;
    private int boardCenterX;
    public ArrayList<AID> redsCorpses = new ArrayList<>();
    public ArrayList<AID> bluesCorpses = new ArrayList<>();

    public World(ServerAgent serverAgent, ArrayList<Pair<AgentType, Integer>> blues, ArrayList<Pair<AgentType, Integer>> reds) {
        this.server = serverAgent;
        this.boardCenterX = (int) server.getFrame().getOptionsPanel().getBoardWidth().getValue();

        Director generator = new Director();

        iterateOverAgentsList("agentBlue_", blues, generator,2, AgentsSides.Blues);
        iterateOverAgentsList("agentRed_",reds,generator,40, AgentsSides.Reds);
    }

    private void iterateOverAgentsList(String agentPrefix, ArrayList<Pair<AgentType,Integer>> list, Director generator, int xPosition, AgentsSides agentSide) {
        int counter = 1;
        for (Pair<AgentType, Integer> p : list) {
            OptionsPanel panel = server.m_frame.getOptionsPanel();
            AgentBuilder builder = chooseBuilder(p.getKey(), agentSide == AgentsSides.Reds ? panel.redPanel : panel.bluePanel);
            PlatformController container = server.getContainerController();
            generator.setAgentBuilder(builder);
            generator.setPlatform(container);
            for (int i=0;i<p.getValue();i++) {
                addAgentsToWorld(builder,p.getKey(),agentSide,generator,counter,agentPrefix, xPosition*p.getKey().getSize());
                counter++;
            }
        }
        offset += counter;
    }

    private AgentBuilder chooseBuilder(AgentType type, SideOptionPanel sideOptionPanel) {
        AgentBuilder.Settings s = new AgentBuilder.Settings();
        s.server = server.getAID();
        s.world = this;
        switch (type) {
            case ARCHER:
                return new ArcherBuilder(s, BerserkBehaviour.class);
            case COMMANDER:
                return new CommanderBuilder(s, BerserkBehaviour.class);
            default:
                s.condition = sideOptionPanel.getCondition();
                s.speed = sideOptionPanel.getSpeed();
                s.strength = sideOptionPanel.getStrength();
                s.accuracy = sideOptionPanel.getAccuracy();
                s.attackRange = sideOptionPanel.getRange();
                return new WarriorBuilder(s, BerserkBehaviour.class);
        }
    }

    private void addAgentsToWorld(AgentBuilder builder, AgentType type, AgentsSides agentSide, Director generator, int counter, String agentPrefix, int xPosition) {
        AgentBuilder.Settings s = builder.getSettings();
        s.name = agentPrefix + (counter + offset);
        AgentInTree ait = new AgentInTree("", agentSide, new Point2D(xPosition, counter * type.getSize()), type, builder.getBehaviour());
        s.position = ait;
        builder.setSettings(s);
        generator.constructAgent();
        AgentController agent;
        try {
            agent = generator.getAgent();
            ait.setAgentName(agent.getName());

            agent.start();

            switch (agentSide) {
                case Blues:
                    bluesAgents.add(new AID(agent.getName(), true));
                    break;
                default:
                    redsAgents.add(new AID(agent.getName(), true));
            }
            try {
                agentsTree.insert(new double[] {ait.p.getX(), ait.p.getY()}, ait);
            } catch (KeySizeException | KeyDuplicateException e) {
                e.printStackTrace();
            }
        } catch (ControllerException e) {
            e.printStackTrace();
        }

    }

    public World(ServerAgent server, HashMap<String,ArrayList<JSONObject>> map, int boardWidth) {
        int counter = 0;
        this.server = server;
        boardCenterX = boardWidth;
        PlatformController container = server.getContainerController();
        Director generator = new Director();

        AgentBuilder.Settings s = new AgentBuilder.Settings();
        s.server = server.getAID();
        s.world = this;

        AgentBuilder warrior = new WarriorBuilder(s, null);
        AgentBuilder archer = new ArcherBuilder(s, null);
        AgentBuilder commander = new CommanderBuilder(s, null);

        for (String type : map.keySet()) {
            ArrayList<JSONObject> list = map.get(type);
            switch (type.toLowerCase()) {
                case "warrior":
                    generator.setAgentBuilder(warrior);
                    generator.setPlatform(container);
                    for (JSONObject agent : list) {
                        addAgentToWorld(agent, warrior, AgentType.WARRIOR, generator, counter);
                        counter++;
                    }
                    break;
                case "archer":
                    generator.setAgentBuilder(archer);
                    generator.setPlatform(container);
                    for (JSONObject agent : list) {
                        addAgentToWorld(agent, archer, AgentType.ARCHER, generator, counter);
                        counter++;
                    }
                    break;
                case "commander":
                    generator.setAgentBuilder(commander);
                    generator.setPlatform(container);
                    for (JSONObject agent : list) {
                        addAgentToWorld(agent, commander, AgentType.COMMANDER, generator, counter);
                        counter++;
                    }
                    break;
                case "obstacle":
                    list.forEach(this::addObstacleToWorld);
            }
        }

        offset += counter + 1;
    }

    private void addObstacleToWorld(JSONObject obstacle) {
        AgentInTree obs = new AgentInTree("obstacle", World.AgentsSides.Obstacle, new Point2D(obstacle.getInt("x"), obstacle.getInt("y")), World.AgentType.OBSTACLE, null);
        try {
            agentsTree.insert(new double[] {obstacle.getInt("x"),obstacle.getInt("y")},obs);
        } catch (KeySizeException e) {
            e.printStackTrace();
        } catch (KeyDuplicateException e) {
            e.printStackTrace();
        }
    }

    private void addAgentToWorld(JSONObject agent, AgentBuilder builder, AgentType type, Director generator, int counter) {
        setBehaviourByFile(builder, agent.get("behaviour").toString());
        AgentsSides side;
        switch (agent.get("side").toString().toLowerCase()) {
            case "blues":
                side = AgentsSides.Blues;
                break;
            case "reds":
                side = AgentsSides.Reds;
                break;
            default:
                side = AgentsSides.Obstacle;
                break;
        }
        AgentBuilder.Settings s = builder.getSettings();
        s.name = "agent_" + (counter + offset);

        AgentInTree ait = new AgentInTree("", side, new Point2D(agent.getInt("x"), agent.getInt("y")), type, builder.getBehaviour());
        s.position = ait;

        builder.setSettings(s);

        generator.constructAgent();

        try {
            AgentController a = generator.getAgent();

            ait.setAgentName(a.getName());
            a.start();
            switch (side) {
                case Blues:
                    bluesAgents.add(new AID(a.getName(), true));
                    break;
                case Reds:
                    redsAgents.add(new AID(a.getName(), true));
                    break;
                default:
                    break;
            }
            try {
                agentsTree.insert(new double[] {ait.p.getX(), ait.p.getY()}, ait);
            } catch (KeySizeException | KeyDuplicateException e) {
                e.printStackTrace();
            }
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }

    private void setBehaviourByFile(AgentBuilder b, String behaviour) {
        switch (behaviour.toLowerCase()) {
            case "berserkbehaviour":
                b.setBehaviourClass(BerserkBehaviour.class);
                break;
            case "commanderbehaviour":
                b.setBehaviourClass(CommanderBehaviour.class);
                break;
        }
    }

    public KDTree<AgentInTree> getAgentsTree() {
        return agentsTree;
    }

    public void clean() {
        //if ((bluesAgents.size() | redsAgents.size()) != 0) {
            ACLMessage m = new ACLMessage(ACLMessage.REQUEST);
            m.setConversationId(ReactiveBehaviour.DELETE);
            bluesAgents.forEach(m::addReceiver);
            redsAgents.forEach(m::addReceiver);
            redsCorpses.forEach(m::addReceiver);
            bluesCorpses.forEach(m::addReceiver);
            server.send(m);
            //bluesAgents.clear();
            //redsAgents.clear();
            //corpses.clear();
        //}
    }

    public void removeAgent(AgentWithPosition agent) {
        if (!agent.position.isDead) {
            try {
                agentsTree.delete(new double[] {agent.position.p.getX(), agent.position.p.getY()});
            } catch (KeySizeException | KeyMissingException e) {
                e.printStackTrace();
            }
            switch (agent.position.side) {
                case Blues:
                    bluesAgents.remove(agent.getAID());
                    break;
                case Reds:
                    redsAgents.remove(agent.getAID());
                    break;
            }
        }
        //server.updateState();
    }

    public AgentInTree getNearestEnemy(AgentWithPosition agent) {
        double[] key = {agent.position.p.getX(), agent.position.p.getY()};
        try {
            List<AgentInTree> t = agentsTree.nearest(key, 1, v -> v.side != agent.position.side && v.side != AgentsSides.Obstacle);
            if (t != null)
                if (!t.isEmpty())
                    return t.get(0);
        } catch (KeySizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized boolean moveAgent(CannonFodder agent, Point2D destination) {
        AgentInTree position = agent.getPosition();
        double[] oldPos = {position.p.getX(), position.p.getY()};
        double[] newPos = {destination.getX(), destination.getY()};

        try {
            //if (agentsTree.nearestEuclidean(oldPos,agent.position.type.getSize()-15).size() > 1)
            //    return false;
            //System.out.println("Move");
            if (agentsTree.search(newPos) != null)
                return false;
        } catch (KeySizeException e) {
            e.printStackTrace();
        }
        position.setPosition(destination);
        try {
            agentsTree.delete(oldPos,true);
            agentsTree.insert(newPos, position);
        } catch (KeySizeException | KeyDuplicateException | KeyMissingException e) {
            e.printStackTrace();
        }
        agent.position = position;
        return true;

    }

    public int[] countFriendFoe(AgentWithPosition agent) {

        int vec[] = new int[2];

        try {
            List<AgentInTree> lst = agentsTree
                    .nearestEuclidean(new double[]{agent.position.p.getX(), agent.position.p.getY()}, agent.fieldOfView)
                    .parallelStream()
                    .filter(e -> e.side != AgentsSides.Obstacle).collect(Collectors.toList());
            vec[0] = (int) lst
                    .parallelStream()
                    .filter(l -> l.side == agent.position.side)
                    .count();
            vec[1] = lst.size() - vec[0];
            //vec[0] = (int) agentsTree.nearestEuclidean(key, agent.fieldOfView).stream().filter(a -> agent.position.side == a.side).count();
        } catch (KeySizeException e) {
            e.printStackTrace();
        }

        /*try {
            vec[1] = (int) agentsTree.nearestEuclidean(key, agent.fieldOfView).stream().filter(a -> agent.position.side != a.side && a.side != AgentsSides.Obstacle).count();
        } catch (KeySizeException e) {
            e.printStackTrace();
        }*/

        return vec;
    }

    public List<AgentInTree> getNeighborFriends(AgentWithPosition agent) {
        //List<AgentInTree> friendlyNeighbors = new ArrayList<>();
        try {
            return agentsTree
                    .nearestEuclidean(new double[] {agent.position.p.getX(), agent.position.p.getY()}, agent.fieldOfView)
                    .parallelStream()
                    .filter(a -> a.side == agent.position.side).collect(Collectors.toList());
        } catch (KeySizeException e) {
            e.printStackTrace();
        }
        return new ArrayList<AgentInTree>();
    }

    public void killAgent(AgentWithPosition agent) {
        if (agent.position.isDead)
            return;
    	
        agent.position.isDead = true;

        //agents.rmPoint(agent.getPosition());

        try {
            double[] agentKey = {agent.position.p.getX(), agent.position.p.getY()};
            if (agentsTree.search(agentKey) != null) {
                agentsTree.delete(agentKey);
            }
        } catch (KeySizeException | KeyMissingException e) {
            e.printStackTrace();
        }
        if (bluesAgents.contains(agent.getAID())) {
            bluesAgents.remove(agent.getAID());
            bluesCorpses.add(agent.getAID());
        }
        else if (redsAgents.contains(agent.getAID())) {
            redsAgents.remove(agent.getAID());
            redsCorpses.add(agent.getAID());
        }
    }

    public double computeBoardCenter(Point2D position) {
        double X = position.getX();
        double returnVal;
        returnVal = (boardCenterX / 2 - X);
        if (returnVal != 0)
            returnVal = returnVal / Math.abs(returnVal);
        return returnVal;
    }

    public enum AgentsSides {Blues, Reds, Obstacle}

    public enum AgentType {
        WARRIOR("res" + File.separator + "warrior.png",20),
        ARCHER("res" + File.separator + "archer.png",20),
        COMMANDER("res" + File.separator + "commander.png",20),
        OBSTACLE("res" + File.separator + "obstacle.png",40);

        private String imagePath;
        private int size;

        private AgentType(String pathToImage, int size) {
            imagePath = pathToImage;
            this.size = size;
        }

        public String getImagePath() {
            return imagePath;
        }

        public int getSize() {
            return size;
        }
    }
}
