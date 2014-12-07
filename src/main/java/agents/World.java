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
import main.java.utils.AgentBuilder;
import main.java.utils.AgentInTree;
import main.java.utils.Director;
import main.java.utils.WarriorBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by Marek on 2014-11-15.
 * Gives possibility for agents to figure out where are they
 */
public class World {


    private static int offset = 0;

    public enum AgentsSides {Blues, Reds}
    public enum AgentType {
        WARRIOR("res" + File.separator + "warrior.png"), ARCHER("res" + File.separator + "archer.png");

        private String value;
        private AgentType(String pathToImage) {
            value = pathToImage;
        }

        public String getValue() {
            return value;
        }

    }
    private final KDTree<AgentInTree> agentsTree = new KDTree<>(2);

    private int boardCenterX;

    public KDTree<AgentInTree> getAgentsTree() {
        return agentsTree;
    }

    private ArrayList<AID> corpses = new ArrayList<>();
    public ArrayList<AID> bluesAgents = new ArrayList<>();
    public ArrayList<AID> redsAgents = new ArrayList<>();

    public ServerAgent server;

    public World(ServerAgent server, int bluesAgentsNumber, int redsAgentsNumber) {

        this.server = server;
        this.boardCenterX = (int) server.getFrame().getOptionsPanel().getBoardWidth().getValue();

        PlatformController container = server.getContainerController();

        try {
            AgentBuilder warrior = new WarriorBuilder(server.getAID(), BerserkBehaviour.class, this);
            Director generator = new Director();
            generator.setAgentBuilder(warrior);
            generator.setPlatform(container);

            for (int i = 0; i < bluesAgentsNumber; i++) {
                String agentName = "agentBlue_" + (i + offset);

                AgentInTree ait = new AgentInTree("", AgentsSides.Blues, new Point2D(2, i), AgentType.WARRIOR);
                warrior.setAgentName(agentName);
                warrior.setPosition(ait);
                generator.constructAgent();

                AgentController agent = generator.getAgent();
                ait.setAgentName(agent.getName());

                agent.start();

                bluesAgents.add(new AID(agent.getName(), true));

                double[] key = {ait.p.getX(), ait.p.getY()};

                try {
                    agentsTree.insert(key, ait);
                } catch (KeySizeException | KeyDuplicateException e) {
                    e.printStackTrace();
                }
            }

            offset += bluesAgentsNumber;

            for (int i = 0; i < redsAgentsNumber; i++) {
                String agentName = "agentRed_" + i + offset;
                AgentInTree ait = new AgentInTree("", AgentsSides.Reds, new Point2D(10, i), AgentType.WARRIOR);

                warrior.setAgentName(agentName);
                warrior.setPosition(ait);
                generator.constructAgent();

                AgentController agent = generator.getAgent();

                ait.setAgentName(agent.getName());
                agent.start();
                redsAgents.add(new AID(agent.getName(), true));

                double[] key = {ait.p.getX(), ait.p.getY()};

                try {
                    agentsTree.insert(key, ait);
                } catch (KeySizeException | KeyDuplicateException e) {
                    e.printStackTrace();
                }
            }
            offset += redsAgentsNumber;

        } catch (ControllerException e) { // finally i have found exceptions useful :D:D
            e.printStackTrace();

        }
    }


    public void clean() {
        if ((bluesAgents.size() | redsAgents.size()) != 0) {
            ACLMessage m = new ACLMessage(ACLMessage.REQUEST);
            m.setConversationId(ReactiveBehaviour.DELETE);
            bluesAgents.forEach(m::addReceiver);
            redsAgents.forEach(m::addReceiver);
            corpses.forEach(m::addReceiver);
            server.send(m);
        }
    }

    public void removeAgent(AgentWithPosition agent) {
        if (!agent.position.isDead) {
            double[] agentKey = {agent.position.p.getX(), agent.position.p.getY()};
            try {
                agentsTree.delete(agentKey);
            } catch (KeySizeException | KeyMissingException e) {
                e.printStackTrace();
            }
            //agents.rmPoint(cannonFodder.getPosition());
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
            List<AgentInTree> t = agentsTree.nearest(key, 1, v -> v.side != agent.position.side);
            if (t != null)
                if (!t.isEmpty())
                    return t.get(0);
        } catch (KeySizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized boolean moveAgent(CannonFodder agent, Point2D destination) {
        Pair borderSize = server.m_frame.getOptionsPanel().getBoardSize();
        double[] key = {destination.getX(), destination.getY()};
        try {
            if (destination.getX() >= (int) borderSize.getKey() || destination.getX() < 0
                    || destination.getY() >= (int) borderSize.getValue() || destination.getY() < 0
                    || agentsTree.search(key) != null)
                return false;
        } catch (KeySizeException e) {
            e.printStackTrace();
        }

        AgentInTree position = agent.getPosition();
        double[] newPos = {destination.getX(), destination.getY()};
        double[] oldPos = {position.p.getX(), position.p.getY()};
        position.setPosition(destination);
        try {
            agentsTree.delete(oldPos);
            agentsTree.insert(newPos, position);
        } catch (KeySizeException | KeyDuplicateException | KeyMissingException e) {
            e.printStackTrace();
        }
        agent.position = position;
        return true;

    }

    public synchronized int[] countFriendFoe(AgentWithPosition agent) {

        double[] key = {agent.position.p.getX(), agent.position.p.getY()};
        int vec[] = new int[2];

        try {
            vec[0] = (int) agentsTree.nearestEuclidean(key, agent.fieldOfView).stream().filter(a -> agent.position.side == a.side).count();
        } catch (KeySizeException e) {
            e.printStackTrace();
        }

        try {
            vec[1] = (int) agentsTree.nearestEuclidean(key, agent.fieldOfView).stream().filter(a -> agent.position.side != a.side).count();
        } catch (KeySizeException e) {
            e.printStackTrace();
        }

        return vec;
    }

    public List<AgentInTree> getNeighborFriends(AgentWithPosition agent, AgentsSides friendlySide) {
        List<AgentInTree> friendlyNeighbors = new ArrayList<>();
        double[] key = {agent.position.p.getX(), agent.position.p.getY()};
        try {
            agentsTree.nearestEuclidean(key, agent.fieldOfView).stream().filter(a -> a.side == friendlySide).forEach(friendlyNeighbors::add);
        } catch (KeySizeException e) {
            e.printStackTrace();
        }
        return friendlyNeighbors;

    }

    public void killAgent(AgentWithPosition agent) {
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
        if (bluesAgents.contains(agent.getAID()))
            bluesAgents.remove(agent.getAID());
        else if (redsAgents.contains(agent.getAID()))
            redsAgents.remove(agent.getAID());

        corpses.add(agent.getAID());
    }

    public double computeBoardCenter(Point2D position) {
        double X = position.getX();
        double returnVal;
        returnVal = (boardCenterX / 2 - X);
        if (returnVal != 0)
            returnVal = returnVal / Math.abs(returnVal);
        return returnVal;
    }
}
