package main.java.agents;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

/**
 * Created by Jakub Fortunka on 20.11.14.
 */
public abstract class ReactiveBehaviour extends Behaviour {

    protected CannonFodder agent = (CannonFodder) this.myAgent;
    protected int state = 0;
    protected World.AgentInTree enemy;

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            // probably can do it with msg.getPrformative and some ACL static fields, but for now let it be string
            switch(msg.getContent()) {
                case "new-step":
                    decideOnNextStep();
                    break;
                case "battle-ended":
                    System.out.println("WE WON!!");
                    state = 2;
                    return;
            }
        } else {
            block();
        }
    }

    @Override
    public boolean done() {
        return state == 2;
    }

    public abstract void decideOnNextStep();
}
