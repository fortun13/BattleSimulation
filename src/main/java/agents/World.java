package main.java.agents;

import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;
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

    private ServerAgent server;

    //Maybe it should be used differently - but, to add new agents, we have to have some agent object running
    //private ServerAgent server;

    // some kind of world representation

    //Tree agents;

    public World() {
        //initialize empty tree
        KdTree.StdKd<AgentComparator.AgentSpace> tmp;
        try {
            tmp = new KdTree.StdKd<>(new ArrayList<>(), new AgentComparator());
        } catch (KdTree.KdTreeException e) {
            tmp = null;
            e.printStackTrace();
        }
        agents = tmp;
    }

    public World(ServerAgent server, int bluesAgentsNumber, int redsAgentsNumber) {
        //initialize tree
        //start agents
        ucho
    }

    public World(ServerAgent server) {
        int agentsNumber = 10;
        PlatformController container = server.getContainerController();

        KdTree.StdKd<AgentComparator.AgentSpace> tmp;
        try {
            List<KdTree.Placed> l = new ArrayList<>(agentsNumber);

            for (int i = 0; i < agentsNumber; i++) {
                String agentName = "agentType_" + i;
                AgentController agent = container.createNewAgent(agentName, "main.java.agents.Warrior", null/*, arguments*/);

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

        agents = tmp;
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

    public AgentWithPosition getNearestEnemy(CannonFodder agent) {
        // same reasoning as down below
        try {
            return (AgentWithPosition) agents.kNearestNeighbours(2, agent).get(1);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public AgentWithPosition getNearestNeighbor(AgentWithPosition agent) {
        //in this case, i think it's better to implement it in the tree - then we won't need to return list
        // when we find neighbor - boom, return it
        HashSet<AgentsSides> set = new HashSet<>();
        set.add(agent.getAgentSide());
        try {
            return (CannonFodder) agents.fetchElements(new AgentComparator.AgentSpace(set)).get(1);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
    
    private AgentWithPosition[] getNeighbors(AgentWithPosition agent) {
        // I assume that list will be sorted from closest to farthest

        // I don't understand; it should return agents from whole map???
        return null;
    }

    public void attack(CannonFodder attacker, CannonFodder attacked) {
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
    }

    private void killAgent(CannonFodder agent) {
        agents.rmPoint(new AgentInTree(agent.getName(),agent.getAgentSide(),agent.getPosition()));
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
                this.agentSides = agentSides;
            }

            public HashSet<AgentsSides> getAgentSide() {
                return agentSides;
            }
        }
    }
}
