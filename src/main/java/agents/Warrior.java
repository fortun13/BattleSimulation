package main.java.agents;

import jade.wrapper.AgentController;

/**
 * Created by Marek on 2014-11-11.
 * Represents an Warrior
 */
public class Warrior extends CannonFodder {

    private AgentController agent;

    public Warrior(AgentController agent) {

        this.agent = agent;
    }

    protected void setup() {
    super.setup();
    }


    protected void takeDown() {

    }


}
