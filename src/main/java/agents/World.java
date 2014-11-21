package main.java.agents;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import main.java.utils.AgentBuilder;
import main.java.utils.Director;
import main.java.utils.KdTree;
import main.java.utils.WarriorBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Marek on 2014-11-15.
 * Gives possibility for agents to figure out where are they
 */
public class World {

    private final KdTree.StdKd<AgentComparator.AgentSpace> agents;

    public ArrayList<AID> bluesAgents = new ArrayList<>();
    public ArrayList<AID> redsAgents = new ArrayList<>();

    /*public ArrayList<AID> getBluesAgents() {
        return bluesAgents;
    }

    public ArrayList<AID> getRedsAgents() {
        return redsAgents;
    }*/

    public AID getAID() {
        return server.getAID();
    }

    public enum AgentsSides {Blues, Reds}

    public class AgentInTree implements KdTree.Placed {

        Point2D p;
        AgentsSides side;
        String agentName;

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

    private ServerAgent server;

    /*public World() {
        //initialize empty tree
        KdTree.StdKd<AgentComparator.AgentSpace> tmp;
        try {
            tmp = new KdTree.StdKd<>(new ArrayList<>(), new AgentComparator());
        } catch (KdTree.KdTreeException e) {
            tmp = null;
            e.printStackTrace();
        }
        agents = tmp;
    }*/

    public World(ServerAgent server, int bluesAgentsNumber, int redsAgentsNumber) {
        //initialize tree
        //start agents
        //agents = null;

        this.server = server;

        PlatformController container = server.getContainerController();

        KdTree.StdKd<AgentComparator.AgentSpace> tmp;
        try {
            //TODO factory or smth...
            List<KdTree.Placed> l = new ArrayList<>(bluesAgentsNumber + redsAgentsNumber);

            //ArrayList<Object> bluesArguments = getAgentArguments(new BerserkBehaviour(), 40, 5, 3, 90, AgentsSides.Blues, this);
            //ArrayList<Object> redsArguments = getAgentArguments(new BerserkBehaviour(), 40, 5, 3, 90, AgentsSides.Reds, this);

            AgentBuilder warrior = new WarriorBuilder(new BerserkBehaviour(),AgentsSides.Blues,this);
            Director generator = new Director();
            generator.setAgentBuilder(warrior);
            generator.setPlatform(container);

            for (int i = 0; i < bluesAgentsNumber; i++) {
                String agentName = "agentBlue_" + i;
                AgentInTree ait = new AgentInTree("", AgentsSides.Blues, new Point2D(2, i + 1));

                warrior.setAgentName(agentName);
                warrior.setPosition(ait);
                generator.constructAgent();
                //bluesArguments.add(ait);
                //AgentController agent = container.createNewAgent(agentName, "main.java.agents.Warrior", bluesArguments.toArray());

                AgentController agent = generator.getAgent();
                ait.setAgentName(agent.getName());

                l.add(ait);

                agent.start();

                bluesAgents.add(new AID(agent.getName(), true));
            }

            warrior.setSide(AgentsSides.Reds);

            for (int i = 0; i < redsAgentsNumber; i++) {
                String agentName = "agentRed_" + i;
                AgentInTree ait = new AgentInTree("", AgentsSides.Reds, new Point2D(10, i + 1));

                warrior.setAgentName(agentName);
                warrior.setPosition(ait);
                generator.constructAgent();
                //redsArguments.add(ait);
                //AgentController agent = container.createNewAgent(agentName, "main.java.agents.Warrior", redsArguments.toArray());

                AgentController agent = generator.getAgent();

                ait.setAgentName(agent.getName());
                l.add(ait);


                agent.start();

                redsAgents.add(new AID(agent.getName(), true));
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

        agents = tmp;
    }

    /*public World(ServerAgent server) {

        this(server,10,10);
    }*/

    /*private ArrayList<Object> getAgentArguments(Behaviour b, int cond, int str, int sp, int acc, AgentsSides s, World w) {
        //return new ArrayList<Object>().addAll({b,cond,str,sp,acc,s,w});
        ArrayList<Object> tmp = new ArrayList<>();
        Collections.addAll(tmp,b,cond,str,sp,acc,s,w);
        return tmp;
    }*/

    public AgentInTree getNearestEnemy(AgentWithPosition agent) {
        HashSet<AgentsSides> set = new HashSet<>();
        for (AgentsSides side : AgentsSides.values()) {
            if (side != agent.getAgentSide()) {
                set.add(side);
            }
        }

        return (AgentInTree) agents.nearestNeighbour(agent.getPosition(), new AgentComparator.AgentSpace(set));
    }

    public boolean moveAgent(CannonFodder agent, Point2D destination) {
        //TODO
        if (agents.isOccupied(agent.getPosition()))
            return false;
        else {
            agent.getPosition().setPosition(destination);
            return true;
        }
        //return false; //żeby się nie czepiał :D
    }

    /*public AgentInTree getNearestNeighbor(CannonFodder agent) {
        HashSet<AgentsSides> set = new HashSet<>();
        set.add(agent.getAgentSide());
        
        return (AgentInTree) agents.nearestNeighbour(agent.getPosition(), new AgentComparator.AgentSpace(set));
    }*/

    public int[] countFriendFoe(AgentWithPosition agent, AgentsSides friendlySide, AgentsSides enemySide){
        int vec[] = new int[2];
        int fov = agent.getFieldOfView();
        //number of friends
        HashSet<AgentsSides> friends = new HashSet<>();
        friends.add(enemySide);
        vec[0] = this.agents.fetchElements(new AgentComparator.AgentSpace(friends,
                new Circle(agent.getPosition().pos().getX(), agent.getPosition().pos().getY(), fov))).size();
        //number of enemies
        HashSet<AgentsSides> enemies = new HashSet<>();
        enemies.add(enemySide);
        vec[1] = this.agents.fetchElements(new AgentComparator.AgentSpace(enemies,
                new Circle(agent.getPosition().pos().getX(), agent.getPosition().pos().getY(), fov))).size();
        return vec;
    }

    public void killAgent(CannonFodder agent) {
        agents.rmPoint(agent.getPosition());
        PlatformController container = server.getContainerController();
        try {
            container.getAgent(agent.getName()).kill();
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }

    private static class AgentComparator extends KdTree.CircleComparator<AgentComparator.AgentSpace> {
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

            public AgentSpace(HashSet<AgentsSides> agentSides) {
                this(agentSides, new Circle(Double.MAX_VALUE));
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
}
