package main.java.agents;

import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;

/**
 * Created by Marek on 2014-11-15.
 */
public class World {

    public enum AgentsSides { Blues, Reds };

    //Maybe it should be used differently - but, to add new agents, we have to have some agent object running
    //private ServerAgent server;

    // some kind of world representation

    //Tree agents;

    public World() {
        //initialize empty tree
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

    public CannonFodder getNearestEnemy(CannonFodder agent) {
        // same reasoning as down below
        return agents.getNearestEnemy(agent);
    }
    
    public CannonFodder getNearestNeighbor(CannonFodder agent) {
        //in this case, i think it's better to implement it in the tree - then we won't need to return list
        // when we find neighbor - boom, return it
        return agents.getNearestNeighbor(agent);
    }
    
    private CannonFodder[] getNeighbors(CannonFodder agent) {
        // I assume that list will be sorted from closest to farthest
        
        return agents.getNeighbors(agent);
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

}
