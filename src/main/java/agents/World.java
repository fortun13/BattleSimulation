package main.java.agents;

import jade.core.AID;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import main.java.utils.KdTree;

import java.util.ArrayList;

/**
 * Created by Marek on 2014-11-15.
 */
public class World {

    static class AgentInTree implements KdTree.Placed {

        Point2D p;
        String agentName;
        AgentsSides side;

        public AgentInTree(String name, AgentsSides s, Point2D p) {
            this.p = p;
            agentName = name;
            side = s;
        }

        public AgentInTree(String name, AgentsSides s, double x, double y) {
            p = new Point2D(x,y);
            agentName = name;
            side = s;
        }

        @Override
        public Point2D pos() {
            return p;
        }
    }

    public enum AgentsSides { Blues, Reds };

    private KdTree.StdKd agents;

    private ServerAgent server;

    //Maybe it should be used differently - but, to add new agents, we have to have some agent object running
    //private ServerAgent server;

    // some kind of world representation

    //Tree agents;

    public World() {
        //initialize empty tree
    }

    public World(ServerAgent server, int bluesAgentsNumber, int redsAgentsNumber) {
        //initialize tree
        //start agents
    }

    public World(ServerAgent server) {
        int agentsNumber = 10;
        PlatformController container = server.getContainerController();
        try {
            for (int i=0;i<agentsNumber;i++) {
                String agentName = "agentType_" + i;
                AgentController agent = container.createNewAgent(agentName, "main.java.agents.Warrior", arguments);
            }
        } catch (ControllerException e) {
        }
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
        return agents.getNearestEnemy(agent);
    }
    
    public AgentWithPosition getNearestNeighbor(AgentWithPosition agent) {
        //in this case, i think it's better to implement it in the tree - then we won't need to return list
        // when we find neighbor - boom, return it
        return agents.getNearestNeighbor(agent);
    }
    
    private AgentWithPosition[] getNeighbors(AgentWithPosition agent) {
        // I assume that list will be sorted from closest to farthest
        
        return agents.fetchElements(new Circle(agent.getFieldOfView()));
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

}
