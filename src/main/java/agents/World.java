package main.java.agents;

import main.java.utils.KdTree;

/**
 * Created by Marek on 2014-11-15.
 */
public class World {

    public enum AgentsSides { Blues, Reds };

    // some kind of world representation

    //Tree agents;

    public World() {
        //initialize empty tree
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
        if (Math.random()*100 > attacker.getAccuracy()) {
            //attack missed
        } else {
            if (attacked.getCondition() <= attacker.getStrength()) {
                killAgent(attacked);
            } else {
                attacked.setCondition((attacked.getCondition() - attacker.getStrength()));
            }
        }
    }

}
