package main.java.agents;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import main.java.utils.KdTree;

import java.util.*;

/**
 * Created by Marek on 2014-11-15.
 * Gives possibility for agents to figure out where are they
 */
public class World {

    private final KdTree.StdKd<AgentComparator.AgentSpace> agents;

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

        PlatformController container = server.getContainerController();

        KdTree.StdKd<AgentComparator.AgentSpace> tmp;
        try {
            //TODO factory or smth...
            List<KdTree.Placed> l = new ArrayList<>(bluesAgentsNumber+redsAgentsNumber);

            ArrayList<Object> bluesArguments = getAgentArguments(new BerserkerBehaviour(),40,5,3,90,AgentsSides.Blues,this);
            ArrayList<Object> redsArguments = getAgentArguments(new BerserkerBehaviour(),40,5,3,90,AgentsSides.Reds,this);

            for (int i = 0; i < bluesAgentsNumber; i++) {
                String agentName = "agentBlue_" + i;
                AgentInTree ait = new AgentInTree("",AgentsSides.Blues,new Point2D(1,i+1));
                bluesArguments.add(ait);
                AgentController agent = container.createNewAgent(agentName, "main.java.agents.Warrior", bluesArguments.toArray());
                ait.setAgentName(agent.getName());

                l.add(ait);

                agent.start();
            }

            for (int i=0;i<redsAgentsNumber;i++) {
                String agentName = "agentRed_"+i;
                AgentInTree ait = new AgentInTree("",AgentsSides.Reds,new Point2D(1,i+1));
                redsArguments.add(ait);
                AgentController agent = container.createNewAgent(agentName,"main.java.agents.Warrior", redsArguments.toArray());
                ait.setAgentName(agent.getName());
                l.add(ait);


                agent.start();
            }

            tmp = new KdTree.StdKd<>(l, new AgentComparator());
        } catch (ControllerException | KdTree.KdTreeException e) { // finally i have found exceptions useful :D:D
            try {
                tmp = new KdTree.StdKd<>(new ArrayList<>(), new AgentComparator());
            } catch (KdTree.KdTreeException e1) {
                tmp = null;
                e1.printStackTrace();
            }
        }

        agents = tmp;
    }

    public World(ServerAgent server) {

        this(server,10,10);
        /*int agentsNumber = 10;
        PlatformController container = server.getContainerController();

        KdTree.StdKd<AgentComparator.AgentSpace> tmp;
        try {
            List<KdTree.Placed> l = new ArrayList<>(agentsNumber);

            for (int i = 0; i < agentsNumber; i++) {
                String agentName = "agentType_" + i;
                AgentController agent = container.createNewAgent(agentName, "main.java.agents.Warrior", null);

                l.add(new Warrior(agent));
            }

            tmp = new KdTree.StdKd<>(l, new AgentComparator());
        } catch (ControllerException | KdTree.KdTreeException e) { // finally i have found exceptions useful :D:D
            try {
                tmp = new KdTree.StdKd<>(new ArrayList<>(), new AgentComparator());
            } catch (KdTree.KdTreeException e1) {
                tmp = null;
                e1.printStackTrace();
            }
        }

        agents = tmp;*/
    }

//    public World(ServerAgent server) {
//        int agentsNumber = 10;
//        this.server = server;
//        PlatformController container = this.server.getContainerController();
//
//        try {
//            ArrayList<KdTree.Placed> allAgents = new ArrayList<KdTree.Placed>();
//            /*for (int i=0;i<agentsNumber;i++) {
//                String agentName = "agentType_" + i;
//                AgentController agent = container.createNewAgent(agentName, "main.java.agents.Warrior", arguments);
//                allAgents.add(new AgentInTree(agentName,1,i));
//
//            }
//            agents = new KdTree.StdKd(allAgents);*/
//
//            Object[] arguments = new Object[7];
//            arguments[0] = new BerserkerBehaviour();
//            arguments[1] = 40;
//            arguments[2] = 5;
//            arguments[3] = 3;
//            arguments[4] = 90;
//            arguments[5] = AgentsSides.Blues;
//            arguments[6] = this;
//
//            Object[] dummyArguments = new Object[7];
//            arguments[0] = ;
//            arguments[1] = 40;
//            arguments[2] = 5;
//            arguments[3] = 3;
//            arguments[4] = 90;
//            arguments[5] = AgentsSides.Reds;
//            arguments[6] = this;
//
//            String agent1 = "blueAgent_1";
//            AgentController agent = container.createNewAgent(agent1, "main.java.agents.Warrior", arguments);
//            allAgents.add(new AgentInTree(agent1,AgentsSides.Blues,1,1));
//            agent.start();
//            String agent2 = "redDummyAgent_1";
//            AgentController dummyAgent = container.createNewAgent(agent2, "main.java.agents.Warrior", dummyArguments);
//            allAgents.add(new AgentInTree(agent2,AgentsSides.Reds,10,10));
//            dummyAgent.start();
//
//        } catch (ControllerException e) {
//            //TODO
//            e.printStackTrace();
//        } catch (KdTree.KdTreeException e) {
//            //TODO
//            e.printStackTrace();
//        }
//    }

    private ArrayList<Object> getAgentArguments(Behaviour b, int cond, int str, int sp, int acc, AgentsSides s, World w) {
        //return new ArrayList<Object>().addAll({b,cond,str,sp,acc,s,w});
        ArrayList<Object> tmp = new ArrayList<>();
        Collections.addAll(tmp,b,cond,str,sp,acc,s,w);
        return tmp;
    }

    public AgentInTree getNearestEnemy(CannonFodder agent) {
        // same reasoning as down below
        try {
            HashSet<AgentsSides> set = new HashSet<>();
            for (AgentsSides side : AgentsSides.values()) {
                if (side != agent.getAgentSide()) {
                    set.add(side);
                }
            }
            return (AgentInTree) agents.kNearestNeighbours(agent.getPosition(), new AgentComparator.AgentSpace(set), 2).get(1);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
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

    public CannonFodder getNearestNeighbor(CannonFodder agent) {
        //in this case, i think it's better to implement it in the tree - then we won't need to return list
        // when we find neighbor - boom, return it
        HashSet<AgentsSides> set = new HashSet<>();
        set.add(agent.getAgentSide());
        try {
            return (CannonFodder) agents.kNearestNeighbours(agent.getPosition(), new AgentComparator.AgentSpace(set), 2).get(1);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public int[] countFriendFoe(AgentWithPosition agent, AgentsSides friendlySide, AgentsSides enemySide){
        int vec[] = new int[2];
        int fov = agent.getFieldOfView();
        //number of friends
        HashSet<AgentsSides> friends = new HashSet<AgentsSides>();
        friends.add(friendlySide);
        vec[0] = this.agents.fetchElements(new AgentComparator.AgentSpace(friends,
                new Circle(agent.getPosition().pos().getX(), agent.getPosition().pos().getY(), fov))).size();
        //number of enemies
        HashSet<AgentsSides> enemies = new HashSet<AgentsSides>();
        enemies.add(enemySide);
        vec[1] = this.agents.fetchElements(new AgentComparator.AgentSpace(enemies,
                new Circle(agent.getPosition().pos().getX(), agent.getPosition().pos().getY(), fov))).size();
        return vec;
    }


    public Point2D getPosition(AgentWithPosition agent) {
        //TODO how to do it ?
        return null;
    }
    
    private AgentWithPosition[] getNeighbors(AgentWithPosition agent) {
        // I assume that list will be sorted from closest to farthest

        // I don't understand; it should return agents from whole map???
        return null;
    }

    public void attack(CannonFodder attacker, AID enemy, AgentInTree e) {

        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(enemy);
        msg.setContent("get-state");
        attacker.send(msg);
        ACLMessage rpl = attacker.blockingReceive();
        int enemyCondition = Integer.getInteger(rpl.getContent());

        if (attacker instanceof Warrior) {

            if (Math.random() * 100 > attacker.getAccuracy()) {
                //attack missed
            } else {
                if (enemyCondition <= attacker.getStrength()) {
                    killAgent(e);
                } else {
                    attacked.setCondition((attacked.getCondition() - attacker.getStrength()));
                }
            }
        } else if (attacker instanceof Archer) {

        }
    }

    /*public void attack(CannonFodder attacker, CannonFodder attacked) {
        //simple formula for now
        // can actually use for example speed to use more properties
        // i.e. - int speedPenalty = attacked.getSpeed() - attacker.getSpeed();
        //          if (speedPenalty > 0)
        //              Math.random()*100 > (attacker.getAccuracy() - speedPenalty)

        //first of all - check what kind of agent is attacking
        if (attacker instanceof Warrior) {

            if (Math.random() * 100 > attacker.getAccuracy()) {
                //attack missed
            } else {
                if (attacked.getCondition() <= attacker.getStrength()) {
                    killAgent(attacked);
                } else {
                    attacked.setCondition((attacked.getCondition() - attacker.getStrength()));
                }
            }
        } else if (attacker instanceof Archer) {

        }
    }*/

    private void killAgent(AgentInTree agent) {
        agents.rmPoint(agent);
        PlatformController container = server.getContainerController();
        try {
            container.getAgent(agent.getAgentName()).kill();
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }

    private static class AgentComparator extends KdTree.CircleComparator<AgentComparator.AgentSpace> {
        @Override
        public boolean contains(AgentSpace area, KdTree.Placed point) {
            CannonFodder agent = (CannonFodder) point;
            return area.getAgentSide().contains(agent.getAgentSide()) && super.contains(area, point);
        }

        @Override
        public boolean contains(AgentSpace area, ArrayList<KdTree.Placed> points) {
            for (KdTree.Placed p : points) {
                CannonFodder f = (CannonFodder) p;
                if (!area.getAgentSide().contains(f.getAgentSide())) return false;
            }

            return super.contains(area, points);
        }

        @Override
        public boolean intersects(AgentSpace area, ArrayList<KdTree.Placed> points, ArrayList<Boolean> ascending) {
            boolean intersect = false;
            for (KdTree.Placed p : points) {
                CannonFodder f = (CannonFodder) p;
                if (intersect = area.getAgentSide().contains(f.getAgentSide())) {
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
