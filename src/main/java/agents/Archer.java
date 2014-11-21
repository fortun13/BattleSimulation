package main.java.agents;

import jade.core.AID;

/**
 * Created by Jakub Fortunka on 19.11.14.
 */
public class Archer extends CannonFodder {

    private int attackRange;


    public void setup() {

        super.setup();

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
