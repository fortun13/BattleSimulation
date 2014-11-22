package main.java.agents;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

/**
 * Created by Jakub Fortunka on 20.11.14.
 */
public abstract class ReactiveBehaviour extends Behaviour {

    protected int state = 0;
    protected World.AgentInTree enemyPosition;
    protected AID enemy;
    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            // probably can do it with msg.getPrformative and some ACL static fields, but for now let it be string
            switch(msg.getConversationId()) {
                case "new-turn":
                    System.out.println("Next turn!! : " + myAgent.getName());
                    decideOnNextStep();
                    break;
                case "battle-ended":
                    System.out.println("WE WON!!");
                    state = 2;
                    return;
                case "attack":
                    ((CannonFodder)myAgent).reactToAttack(msg.getContent());
                    break;
            }
            ACLMessage m = new ACLMessage(ACLMessage.INFORM);
            //TODO it's not good practice - i just want to test if this will work
            m.addReceiver(new AID("server",false));
            m.setConversationId("ended-computation");
            myAgent.send(m);
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
