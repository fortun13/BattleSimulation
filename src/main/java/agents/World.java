package main.java.agents;

import edu.wlu.cs.levy.CG.*;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.util.Pair;
import main.java.utils.AgentBuilder;
import main.java.utils.Director;
import main.java.utils.KdTree;
import main.java.utils.WarriorBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by Marek on 2014-11-15.
 * Gives possibility for agents to figure out where are they
 */
public class World {

    private final KDTree<AgentInTree> agents2 = new KDTree<AgentInTree>(2);
    private int boardCenterX;


    //private final KdTree.StdKd<AgentComparator.AgentSpace> agents;
    private Semaphore cleared = new Semaphore(0);

    /*public KdTree.StdKd<AgentComparator.AgentSpace> getAgents() {
        return agents;
    }*/

    public KDTree<AgentInTree> getAgents2() {
        return agents2;
    }

    public ArrayList<AID> bluesAgents = new ArrayList<>();
    public ArrayList<AID> redsAgents = new ArrayList<>();

    public void clean() {
        if ((bluesAgents.size() | redsAgents.size()) != 0) {
            ACLMessage m = new ACLMessage(ACLMessage.REQUEST);
            m.setConversationId(ReactiveBehaviour.DELETE);
            bluesAgents.forEach(m::addReceiver);
            redsAgents.forEach(m::addReceiver);
            server.send(m);

            try {
                cleared.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeAgent(CannonFodder cannonFodder) {
        if (!cannonFodder.position.isDead) {
            double[] agentKey = {cannonFodder.position.p.getX(), cannonFodder.position.p.getY()};
            try {
                agents2.delete(agentKey);
            } catch (KeySizeException e) {
                e.printStackTrace();
            } catch (KeyMissingException e) {
                e.printStackTrace();
            }
            //agents.rmPoint(cannonFodder.getPosition());
            switch (cannonFodder.side) {
                case Blues:
                    bluesAgents.remove(cannonFodder.getAID());
                    break;
                case Reds:
                    redsAgents.remove(cannonFodder.getAID());
                    break;
            }
        }
        if ((bluesAgents.size() | redsAgents.size()) == 0) {
            cleared.release();
        }
        //server.updateState();
    }

    /*public ArrayList<AID> getBluesAgents() {
        return bluesAgents;
    }

    public ArrayList<AID> getRedsAgents() {
        return redsAgents;
    }*/

    public enum AgentsSides {Blues, Reds}

    public class AgentInTree implements KdTree.Placed {

        Point2D p;
        public AgentsSides side;
        String agentName;
        public boolean isDead = false;

        public AgentInTree(String agentName,AgentsSides side, Point2D position) {
            this.agentName = agentName;
            this.side = side;
            p = position;
        }

        public String getAgentName() {
            return agentName;
        }

        public void setAgentName(String name) {
            agentName = name;
        }

        @Override
        public Point2D pos() {
            return p;
        }

        public void setPosition(Point2D p) {
            this.p = p;
        }
    }

    public ServerAgent server;

    public World(ServerAgent server, int bluesAgentsNumber, int redsAgentsNumber) {

        this.server = server;
        this.boardCenterX = (int)server.getFrame().getOptionsPanel().getBoardWidth().getValue();

        PlatformController container = server.getContainerController();

        KdTree.StdKd<AgentComparator.AgentSpace> tmp;
        try {
            List<KdTree.Placed> l = new ArrayList<>(bluesAgentsNumber + redsAgentsNumber);

            AgentBuilder warrior = new WarriorBuilder(server.getAID(), BerserkBehaviour.class,AgentsSides.Blues,this);
            Director generator = new Director();
            generator.setAgentBuilder(warrior);
            generator.setPlatform(container);

            /* Maybye problem with returning position out of border is here?
               I've added some code in moveAgent, but it won't help. Now program crashes while initializing agents
               Agent in tree returns the beggining position, so there may be some problem
             */
            for (int i = 0; i < bluesAgentsNumber; i++) {
                String agentName = "agentBlue_" + i;
                /*
                Why i + 1?
                AgentInTree ait = new AgentInTree("", AgentsSides.Blues, new Point2D(2, i + 1));
                 */

                AgentInTree ait = new AgentInTree("", AgentsSides.Blues, new Point2D(2, i));
                warrior.setAgentName(agentName);
                warrior.setPosition(ait);
                generator.constructAgent();

                AgentController agent = generator.getAgent();
                ait.setAgentName(agent.getName());

                l.add(ait);

                agent.start();

                bluesAgents.add(new AID(agent.getName(), true));

                double[] key = {ait.p.getX(),ait.p.getY()};

                try {
                    agents2.insert(key,ait);
                } catch (KeySizeException e) {
                    e.printStackTrace();
                } catch (KeyDuplicateException e) {
                    e.printStackTrace();
                }
            }

            warrior.setSide(AgentsSides.Reds);

            for (int i = 0; i < redsAgentsNumber; i++) {
                String agentName = "agentRed_" + i;
                AgentInTree ait = new AgentInTree("", AgentsSides.Reds, new Point2D(10, i));

                warrior.setAgentName(agentName);
                warrior.setPosition(ait);
                generator.constructAgent();

                AgentController agent = generator.getAgent();

                ait.setAgentName(agent.getName());
                l.add(ait);


                agent.start();

                redsAgents.add(new AID(agent.getName(), true));

                double[] key = {ait.p.getX(),ait.p.getY()};

                try {
                    agents2.insert(key,ait);
                } catch (KeySizeException e) {
                    e.printStackTrace();
                } catch (KeyDuplicateException e) {
                    e.printStackTrace();
                }
            }

            tmp = new KdTree.StdKd<>(l, new AgentComparator());
        } catch (ControllerException | KdTree.KdTreeException e) { // finally i have found exceptions useful :D:D
            e.printStackTrace();
            try {
                tmp = new KdTree.StdKd<>(new ArrayList<>(), new AgentComparator());
            } catch (KdTree.KdTreeException e1) {
                tmp = null;
                e1.printStackTrace();
            }
        }

        //agents = tmp;


    }

    public AgentInTree getNearestEnemy(AgentWithPosition agent) {
        /*HashSet<AgentsSides> set = new HashSet<>();
        for (AgentsSides side : AgentsSides.values()) {
            if (side != agent.getAgentSide()) {
                set.add(side);
            }
        }*/

        //int side = agent.side==AgentsSides.Blues ? -1 : 1;

        //return (AgentInTree) agents.nearestNeighbour(agent.getPosition(), new AgentComparator.AgentSpace(set));
        double[] key = {agent.position.p.getX(),agent.position.p.getY()};
        try {
            List<AgentInTree> t = agents2.nearest(key,1, v -> v.side!=agent.side);
            if (t != null)
                if (!t.isEmpty())
                    return t.get(0);
        } catch (KeySizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized boolean moveAgent(CannonFodder agent, Point2D destination) {
        /*Pair borderSize = server.m_frame.getOptionsPanel().getBoardSize();
        if (destination.getX() >= (int)borderSize.getKey() || destination.getX() < 0
                || destination.getY() >= (int)borderSize.getValue() || destination.getY() < 0
                || agents.isOccupied(new AgentInTree("",AgentsSides.Blues,destination)))
            return false;
        else {
            AgentInTree position = agent.getPosition();
            AgentInTree newPosition = new AgentInTree(position.getAgentName(),position.side,new Point2D(destination.getX(),destination.getY()));
            agents.rmPoint(position);
            //position.setPosition(destination);
            agents.addPoint(position);
            return true;
        }*/

        Pair borderSize = server.m_frame.getOptionsPanel().getBoardSize();
        double[] key = {destination.getX(),destination.getY()};
        try {
        if (destination.getX() >= (int)borderSize.getKey() || destination.getX() < 0
                || destination.getY() >= (int)borderSize.getValue() || destination.getY() < 0
                || agents2.search(key) != null)
            return false;
        } catch (KeySizeException e) {
            e.printStackTrace();
        }

            AgentInTree position = agent.getPosition();
        double[] newPos = {destination.getX(),destination.getY()};
        double[] oldPos = {position.p.getX(),position.p.getY()};
            //AgentInTree newPosition = new AgentInTree(position.getAgentName(), position.side, new Point2D(destination.getX(), destination.getY()));
            //agents.rmPoint(position);
            position.setPosition(destination);
            //agents.addPoint(position);
        try {
            agents2.delete(oldPos);
            agents2.insert(newPos,position);
        } catch (KeySizeException e) {
            e.printStackTrace();
        } catch (KeyMissingException e) {
            e.printStackTrace();
        } catch (KeyDuplicateException e) {
            e.printStackTrace();
        }
        agent.position = position;
        return true;

    }

    public synchronized int[] countFriendFoe(AgentWithPosition agent, AgentsSides friendlySide, AgentsSides enemySide){

        double[] key = {agent.position.p.getX(),agent.position.p.getY()};
        int vec[] = new int[2];

        try {
            vec[0] = (int) agents2.nearestEuclidean(key,agent.fieldOfView).stream().filter(a -> agent.side==a.side).count();
        } catch (KeySizeException e) {
            e.printStackTrace();
        }

        try {
            vec[1] = (int) agents2.nearestEuclidean(key,agent.fieldOfView).stream().filter(a -> agent.side!=a.side).count();
        } catch (KeySizeException e) {
            e.printStackTrace();
        }

        return vec;

        /*int vec[] = new int[2];
        int fov = agent.getFieldOfView();
        //number of friends
        HashSet<AgentsSides> friends = new HashSet<>();
        friends.add(friendlySide);
        vec[0] = this.agents.fetchElements(new AgentComparator.AgentSpace(friends,
                new Circle(agent.getPosition().pos().getX(), agent.getPosition().pos().getY(), fov))).size();
        //number of enemies
        HashSet<AgentsSides> enemies = new HashSet<>();
        enemies.add(enemySide);
        vec[1] = this.agents.fetchElements(new AgentComparator.AgentSpace(enemies,
                new Circle(agent.getPosition().pos().getX(), agent.getPosition().pos().getY(), fov))).size();
        return vec;*/
    }

    public List getNeighborFriends(AgentWithPosition agent, AgentsSides friendlySide){
        List<AgentInTree> friendlyNeighbors = new ArrayList<>();
        double[] key = {agent.position.p.getX(),agent.position.p.getY()};
        try {
            agents2.nearestEuclidean(key,agent.fieldOfView).stream().filter(a -> a.side==friendlySide).forEach(friendlyNeighbors::add);
        } catch (KeySizeException e) {
            e.printStackTrace();
        }
        return friendlyNeighbors;
        /*int fov = agent.getFieldOfView();
        //number of friends

        HashSet<AgentsSides> friends = new HashSet<>();
        friends.add(friendlySide);
        friendlyNeighbors = this.agents.fetchElements(new AgentComparator.AgentSpace(friends,
                new Circle(agent.getPosition().pos().getX(), agent.getPosition().pos().getY(), fov)));
        //number of enemies
        HashSet<AgentsSides> enemies = new HashSet<>();
        return friendlyNeighbors;*/
    }

    public void killAgent(AgentWithPosition agent) {
        agent.position.isDead=true;

        //agents.rmPoint(agent.getPosition());

        try {
            double[] agentKey = {agent.position.p.getX(),agent.position.p.getY()};
            if (agents2.search(agentKey) != null) {
                agents2.delete(agentKey);
            }
        } catch (KeySizeException e) {
            e.printStackTrace();
        } catch (KeyMissingException e) {
            e.printStackTrace();
        }
        if (bluesAgents.contains(agent.getAID()))
            bluesAgents.remove(agent.getAID());
        else if (redsAgents.contains(agent.getAID()))
            redsAgents.remove(agent.getAID());

        /*if (agent.getAgentSide() == AgentsSides.Blues)
            bluesAgents.remove(agent.getAID());
        else
            redsAgents.remove(agent.getAID());*/
        //server.updateState();
        /*PlatformController container = server.getContainerController();
        try {
            container.getAgent(agent.getLocalName()).kill();
        } catch (ControllerException e) {
            e.printStackTrace();
        }*/
    }

    public static class AgentComparator extends KdTree.CircleComparator<AgentComparator.AgentSpace> {
        @Override
        public boolean contains(AgentSpace area, KdTree.Placed point) {
            AgentInTree agent = (AgentInTree) point;
            return area.getAgentSide().contains(agent.side) && super.contains(area, point);
        }

        @Override
        public boolean contains(AgentSpace area, ArrayList<KdTree.Placed> points) {
            for (KdTree.Placed p : points) {
                AgentInTree f = (AgentInTree) p;
                if (!area.getAgentSide().contains(f.side)) return false;
            }

            return super.contains(area, points);
        }

        @Override
        public boolean intersects(AgentSpace area, ArrayList<KdTree.Placed> points, ArrayList<Boolean> ascending) {
            boolean intersect = false;
            for (KdTree.Placed p : points) {
                AgentInTree f = (AgentInTree) p;
                if (intersect = area.getAgentSide().contains(f.side)) {
                    break;
                }
            }
            return intersect && super.intersects(area, points, ascending);
        }

        @Override
        public double distance(KdTree.Placed a, KdTree.Placed b) {
            return super.distance(a, b);
        }

        @Override
        public double distance(KdTree.Placed a, KdTree.Placed b, int dimension) {
            return super.distance(a, b, dimension);
        }

        @Override
        public boolean lower(KdTree.Placed a, KdTree.Placed b, int dimension) {
            return super.lower(a, b, dimension);
        }

        public static class AgentSpace extends Circle {
            private HashSet<AgentsSides> agentSides;
            boolean covered = false;

            @Override
            public boolean contains(Point2D localPoint) {
                return covered || super.contains(localPoint);
            }

            public AgentSpace(HashSet<AgentsSides> agentSides) {
                this.agentSides = agentSides;
                covered = true;
            }

            public AgentSpace(HashSet<AgentsSides> agentSides, Circle searchArea) {
                super(searchArea.getCenterX(), searchArea.getCenterY(), searchArea.getRadius());
                this.agentSides = agentSides;
            }

            public HashSet<AgentsSides> getAgentSide() {
                return agentSides;
            }
        }
    }

    public double computeBoardCenter(Point2D position){
        double X = position.getX();
        double returnVal;
        returnVal = (boardCenterX/2 - X);
        if(returnVal != 0)
            returnVal = returnVal/Math.abs(returnVal) ;
        return returnVal;
    }
}
