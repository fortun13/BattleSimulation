package main.java.agents;

import jade.core.behaviours.Behaviour;

/**
 * Created by Jakub Fortunka on 19.11.14.
 */
public class Archer extends CannonFodder {

    private int attackRange;


    protected void setup(Behaviour b) {

//        super.setup(b);

    }


    protected void takeDown() {

    }

    public int getAttackRange() {
        return attackRange;
    }

    public void setAttackRange(int attackRange) {
        this.attackRange = attackRange;
    }
}
