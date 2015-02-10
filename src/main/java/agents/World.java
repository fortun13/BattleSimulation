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
import main.java.gui.BoardPanel;
import main.java.gui.BoidOptions;
import main.java.gui.SideOptionPanel;
import main.java.utils.*;
import org.json.JSONObject;

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
    /**
     *  Object of KDTree<AgentInTree> in which we are holding positions of all our agents
     */
    private final KDTree<AgentInTree> agentsTree = new KDTree<>(2);
    public ArrayList<AID> bluesAgents = new ArrayList<>();
    public ArrayList<AID> redsAgents = new ArrayList<>();
//    public ServerAgent server;
    private Point2D boardCenter;
    public ArrayList<AID> redsCorpses = new ArrayList<>();
    public ArrayList<AID> bluesCorpses = new ArrayList<>();

    //TODO it should be here? Maybe keep it somewhere else?
    Point2D boardSize;

    //TODO waiting for splitting JPanel from actual options - for now, i just want it to compile
    private BoidOptions boidOptions;

    /**
     * Contructor for this class - mainly it populates world with agents
     *
     * @param serverAgent reference to server agent - needed to sending information about ending computation so next turn can be started
     * @param blues  list of Pairs of AgentType and Integer (their number) of blue side - all agents for blue sides are created given this informations
     * @param reds list of Pairs of AgentType and Integer (their number) of red side - all agents for red sides are created given this informations
     */
    public World(ServerAgent serverAgent, ArrayList<Pair<AgentType, Integer>> blues, ArrayList<Pair<AgentType, Integer>> reds) {
//        this.server = serverAgent;
        Point2D p = serverAgent.getFrame().getBoardPanel().getBoardSize();
        boardCenter = new Point2D((p.getX()-1)/2,(p.getY()-1)/2);
        Director generator = new Director();

        boardSize = serverAgent.getFrame().getBoardPanel().getBoardSize();

        boidOptions = serverAgent.getFrame().getOptionsPanel().options;

        int counter = 1;

        for (Pair<AgentType,Integer> pair : blues) {
            addAgentsToWorld(serverAgent,pair,generator,AgentsSides.Blues,10,"agentBlues_",counter);
            counter += pair.getValue();
        }

        counter = 1;

        for (Pair<AgentType,Integer> pair : reds) {
            addAgentsToWorld(serverAgent,pair,generator, AgentsSides.Reds,(int) (p.getX()/20)-10,"agentReds_",counter);
            counter += pair.getValue();
        }

//        blues.forEach((Pair<AgentType, Integer> pair) -> {
//            addAgentsToWorld(serverAgent, pair, generator, AgentsSides.Blues, 10, "agentBlues_");
//        });
//
//        reds.forEach((Pair<AgentType, Integer> pair) -> {
//            addAgentsToWorld(serverAgent, pair, generator, AgentsSides.Reds, (int) (p.getX() / 20) - 10, "agentReds_");
//        });

        //TODO do we need to use SQUARESIZE?
    }

    private void addAgentsToWorld(ServerAgent serverAgent,Pair<AgentType,Integer> pair, Director generator, AgentsSides side, int xPos,String prefix, int counter) {
        AgentBuilder builder = prepareBuilder(pair.getKey(),
                side == AgentsSides.Blues ? serverAgent.getFrame().getOptionsPanel().bluePanel : serverAgent.getFrame().getOptionsPanel().redPanel,
                serverAgent);
        PlatformController container = serverAgent.getContainerController();
        generator.setAgentBuilder(builder);
        generator.setPlatform(container);
        for (int i=0;i<pair.getValue();i++) {
            addAgentToWorld(builder,pair.getKey(),side,generator,i+1+counter,prefix, xPos*pair.getKey().getSize());
        }
        offset += pair.getValue();
    }

    private void addAgentToWorld(AgentBuilder builder, AgentType type, AgentsSides agentSide, Director generator, int counter, String agentPrefix, int xPosition) {
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

    private AgentBuilder prepareBuilder(AgentType type, SideOptionPanel sideOptionPanel,ServerAgent sa) {
        //TODO can't use ServerAgent - it's temporary (until better design is implemented)
        AgentBuilder.Settings s = new AgentBuilder.Settings();
        s.server = sa.getAID();
        s.world = this;
        s.condition = sideOptionPanel.getCondition(type);
        s.speed = sideOptionPanel.getSpeed(type);
        s.strength = sideOptionPanel.getStrength(type);
        s.accuracy = sideOptionPanel.getAccuracy(type);
        s.attackRange = sideOptionPanel.getRange(type);
        Class<? extends ReactiveBehaviour> behaviour;
        if (type == AgentType.COMMANDER) {
            s.attractionForce = sideOptionPanel.getAttractionForce();
            behaviour = CommanderBehaviour.class;
        }
        else
            behaviour = BerserkBehaviour.class;
        return chooseBuilder(type,s, behaviour);
    }

    private AgentBuilder chooseBuilder(AgentType type, AgentBuilder.Settings settings, Class<? extends ReactiveBehaviour> behaviour) {
        switch(type) {
            case WARRIOR:
                return new WarriorBuilder(settings,behaviour);
            case ARCHER:
                return new ArcherBuilder(settings,behaviour);
            case COMMANDER:
                return new CommanderBuilder(settings,behaviour);
            default:
                return new WarriorBuilder(settings,behaviour);
        }
    }

    /**
     * Constructor for creating the world from file
     *
     * @param server reference to server agent - needed to sending information about ending computation so next turn can be started
     * @param map map of String -> ArrayList<JSONObject>. Key of map (String) is representing type of agent (Warrior, Archer etc.), ArrayList - all agents of that type
     */
    public World(ServerAgent server, HashMap<String,ArrayList<JSONObject>> map) {
        int counter = 0;
//        this.server = server;
        Point2D p = server.getFrame().getBoardPanel().getBoardSize();
        boardCenter = new Point2D((p.getX()-1)/2,(p.getY()-1)/2);
        PlatformController container = server.getContainerController();
        Director generator = new Director();

        for (String type : map.keySet()) {
            ArrayList<JSONObject> list = map.get(type);
            AgentType t = null;
            switch (type.toLowerCase()) {
                case "warrior":
                    t = AgentType.WARRIOR;
//                    generator.setAgentBuilder(warrior);
//                    generator.setPlatform(container);
//                    for (JSONObject agent : list) {
//                        addAgentToWorld(agent, warrior, AgentType.WARRIOR, generator, counter);
//                        counter++;
//                    }
                    break;
                case "archer":
                    t = AgentType.ARCHER;
//                    generator.setAgentBuilder(archer);
//                    generator.setPlatform(container);
//                    for (JSONObject agent : list) {
//                        addAgentToWorld(agent, archer, AgentType.ARCHER, generator, counter);
//                        counter++;
//                    }
                    break;
                case "commander":
                    t = AgentType.COMMANDER;
//                    generator.setAgentBuilder(commander);
//                    generator.setPlatform(container);
//                    for (JSONObject agent : list) {
//                        addAgentToWorld(agent, commander, AgentType.COMMANDER, generator, counter);
//                        counter++;
//                    }
                    break;
                case "obstacle":
                    list.forEach(this::addObstacleToWorld);
                    continue;
            }

            //TODO waiting for flyweight to make applying setting efficient and readable (so for now - reading from file is NOT WORKING)

            AgentBuilder.Settings settings = new AgentBuilder.Settings();
            settings.server = server.getAID();
            settings.world = this;

            AgentBuilder builder = chooseBuilder(t,settings,null);

            list.forEach((JSONObject o) -> {
                //addAgentToWorld(o,builder,t,generator);
            });

        }

        offset += counter + 1;
    }

    private void addObstacleToWorld(JSONObject obstacle) {
        AgentInTree obs = new AgentInTree("obstacle", World.AgentsSides.Obstacle, new Point2D(obstacle.getInt("x"), obstacle.getInt("y")), World.AgentType.OBSTACLE, null);
        try {
            agentsTree.insert(new double[] {obstacle.getInt("x"),obstacle.getInt("y")},obs);
        } catch (KeySizeException | KeyDuplicateException e) {
            e.printStackTrace();
        }
    }

    //TODO change this so it would be more civilized (and more functional-like :) )
//    private void addAgentToWorld(JSONObject agent, AgentBuilder builder, AgentType type, Director generator, int counter) {
//        setBehaviourByFile(builder, agent.get("behaviour").toString());
//        AgentsSides side = null;
//        switch (agent.get("side").toString().toLowerCase()) {
//            case "blues":
//                side = AgentsSides.Blues;
//                break;
//            case "reds":
//                side = AgentsSides.Reds;
//                break;
//        }
//        AgentBuilder.Settings s = builder.getSettings();
//        //TODO
//        s.name = "agent_" + (counter + offset);
//        SideOptionPanel sideOptionPanel = (side == AgentsSides.Blues) ? server.m_frame.getOptionsPanel().bluePanel : server.m_frame.getOptionsPanel().redPanel;
//        s.condition = sideOptionPanel.getCondition(type);
//        s.speed = sideOptionPanel.getSpeed(type);
//        s.strength = sideOptionPanel.getStrength(type);
//        s.accuracy = sideOptionPanel.getAccuracy(type);
//        s.attackRange = sideOptionPanel.getRange(type);
//        if (type == AgentType.COMMANDER)
//            s.attractionForce = sideOptionPanel.getAttractionForce();
//
//        AgentInTree ait = new AgentInTree("", side, new Point2D(agent.getInt("x"), agent.getInt("y")), type, builder.getBehaviour());
//        s.position = ait;
//
//        builder.setSettings(s);
//
//        generator.constructAgent();
//
//        try {
//            AgentController a = generator.getAgent();
//
//            ait.setAgentName(a.getName());
//            a.start();
//            switch (side) {
//                case Blues:
//                    bluesAgents.add(new AID(a.getName(), true));
//                    break;
//                case Reds:
//                    redsAgents.add(new AID(a.getName(), true));
//                    break;
//                default:
//                    break;
//            }
//            try {
//                agentsTree.insert(new double[] {ait.p.getX(), ait.p.getY()}, ait);
//            } catch (KeySizeException | KeyDuplicateException e) {
//                e.printStackTrace();
//            }
//        } catch (ControllerException e) {
//            e.printStackTrace();
//        }
//    }

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

    /**
     * cleans up world before next simulation
     */
    public void clean() {
        //TODO how to make clean up without "server"? 
        ACLMessage m = new ACLMessage(ACLMessage.REQUEST);
        m.setConversationId(ReactiveBehaviour.DELETE);
        bluesAgents.forEach(m::addReceiver);
        redsAgents.forEach(m::addReceiver);
        redsCorpses.forEach(m::addReceiver);
        bluesCorpses.forEach(m::addReceiver);
        //server.send(m);
    }

    /**
     * removes agent from world
     * @param agent reference to agent which will be removed
     */
    public void removeAgent(AgentWithPosition agent) {
        if (!agent.currentState.isDead) {
            try {
                agentsTree.delete(new double[] {agent.currentState.p.getX(), agent.currentState.p.getY()});
            } catch (KeySizeException | KeyMissingException e) {
                e.printStackTrace();
            }
            switch (agent.currentState.side) {
                case Blues:
                    bluesAgents.remove(agent.getAID());
                    break;
                case Reds:
                    redsAgents.remove(agent.getAID());
                    break;
            }
        }
    }

    /**
     * Method returns nearest enemy for agent (or null if there is no enemy in agents field of view)
     *
     * @param agent agent which want to find nearest enemy
     * @return object representing nearest enemy; null if no enemy was found
     */
    public AgentInTree getNearestEnemy(AgentWithPosition agent) {
        try {
            List<AgentInTree> l = agentsTree
                    .nearestEuclidean(new double[] {agent.currentState.p.getX(), agent.currentState.p.getY()},agent.fieldOfView)
                    .parallelStream()
                    .filter(a -> a.side != agent.currentState.side && a.side != AgentsSides.Obstacle)
                    .collect(Collectors.toList());
            if (l != null && !l.isEmpty())
                return l.get(0);
        } catch (KeySizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method changes position of agent in tree
     *
     * @param agent agent which want to change his position
     * @param destination new desired position of agent
     * @return true if changing position was possible and been done; false if desired position is already taken
     */
    public synchronized boolean moveAgent(AgentWithPosition agent, Point2D destination) {
        AgentInTree position = agent.getCurrentState();
        double[] oldPos = {position.p.getX(), position.p.getY()};
        double[] newPos = {destination.getX(), destination.getY()};

        try {
            //Point2D boardSize = server.getFrame().getBoardPanel().getBoardSize();
            if (agentsTree.search(newPos) != null &&
                    newPos[0] > 0 && newPos[0] < boardSize.getX() &&
                    newPos[1] > 0 && newPos[1] < boardSize.getY())
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
        agent.currentState = position;
        return true;

    }

    /**
     * Counts number of friends and foes in field of view of agent
     *
     * @param agent agent which want to know how many friends/foes are in his field of view
     * @return int[0] - number of friends; int[1] - number of foes
     */
    public int[] countFriendFoe(AgentWithPosition agent) {

        int vec[] = new int[2];

        try {
            List<AgentInTree> lst = agentsTree
                    .nearestEuclidean(new double[]{agent.currentState.p.getX(), agent.currentState.p.getY()}, agent.fieldOfView)
                    .parallelStream()
                    .filter(e -> e.side != AgentsSides.Obstacle).collect(Collectors.toList());
            vec[0] = (int) lst
                    .parallelStream()
                    .filter(l -> l.side == agent.currentState.side)
                    .count();
            vec[1] = lst.size() - vec[0];
        } catch (KeySizeException e) {
            e.printStackTrace();
        }
        return vec;
    }

    /**
     *
     * @param agent
     */
    public void killAgent(AgentWithPosition agent) {
        if (agent.currentState.isDead)
            return;

        agent.currentState.isDead = true;
        try {
            double[] agentKey = {agent.currentState.p.getX(), agent.currentState.p.getY()};
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

    public Point2D returnBoardCenter() {
        return boardCenter;
    }

    /**
     * Method updates positions of agents which been dragged & dropped in some place on board
     *
     * @param changed list of agents which position have been changed
     */
    public void updateTree(List<BoardPanel.MyAgent> changed) {
        for (BoardPanel.MyAgent a : changed) {
            AgentInTree position = a.getAgent();
            double[] oldPos = {position.p.getX(), position.p.getY()};
            double[] newPos = {a.getPoint().getX(), a.getPoint().getY()};

            position.setPosition(a.getPoint());
            try {
                agentsTree.delete(oldPos);
                agentsTree.insert(newPos, position);
            } catch (KeySizeException | KeyDuplicateException | KeyMissingException e) {
                e.printStackTrace();
            }
        }
    }

    public BoidOptions getBoidOptions() {
        return boidOptions;
    }

    /**
     * Enum representing side of conflict (also - obstacles)
     */
    public enum AgentsSides {
        Blues("Blues"), Reds("Reds"), Obstacle("Obstacle");

        private String name;
        private AgentsSides(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    /**
     * enum representing types of agents (warior etc.) (also - obstacles)
     */
    public enum AgentType {
        WARRIOR("main/resources/images/warrior.png",20, "Warrior"),
        ARCHER("main/resources/images/archer.png",20, "Archer"),
        COMMANDER("main/resources/images/commander.png",20, "Commander"),
        OBSTACLE("main/resources/images/obstacle.png",40, "Obstacle");

        private String imagePath;
        private int size;
        private String name;

        private AgentType(String pathToImage, int size, String name) {
            imagePath = pathToImage;
            this.size = size;
            this.name = name;
        }

        public String getImagePath() {
            return imagePath;
        }

        public int getSize() {
            return size;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
