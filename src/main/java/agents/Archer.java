package main.java.agents;

import jade.core.AID;

/**
 * Created by Jakub Fortunka on 19.11.14.
 */
public class Archer extends CannonFodder {

    private int attackRange;


    public void setup() {

        Object[] parameters = getArguments();

        addBehaviour((ReactiveBehaviour) parameters[0]);
        this.condition = (int) parameters[1];
        this.strength = (int) parameters[2];
        this.speed = (int) parameters[3];
        this.accuracy  = (int) parameters[4];
        this.side = (World.AgentsSides) parameters[5];
        this.world = (World) parameters[6];
        this.position = (World.AgentInTree) parameters[7];
        this.attackRange = (int) parameters[8];

    }


    protected void takeDown() {

    }

    @Override
    protected void attack(AID enemy) {

    }


    public int getAttackRange() {
        return attackRange;
    }

    public void setAttackRange(int attackRange) {
        this.attackRange = attackRange;
    }

    @Override
    public boolean enemyInRangeOfAttack(World.AgentInTree enemy) {
        return getPosition().pos().distance(enemy.pos()) < (2+getAttackRange());
    }

    @Override
    public void reactToAttack(String content) {

    }
}
