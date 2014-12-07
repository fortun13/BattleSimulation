package main.java.agents;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import main.java.utils.AgentInTree;

/**
 * Created by Jakub Fortunka on 20.11.14.
 *
 */
public abstract class ReactiveBehaviour extends Behaviour {

    public static final String DELETE = "DELETE";
    protected int state = 0;
    protected AgentInTree enemyPosition;
    protected AID enemy;
    protected AID serverAID;

    protected ReactiveBehaviour(AID serverAID) {
        this.serverAID = serverAID;
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            // probably can do it with msg.getPrformative and some ACL static fields, but for now let it be string
            switch(msg.getConversationId()) {
                case "enemy-dead":
                    enemy = null;
                    enemyPosition = null;
                    break;
                case "new-turn":
                    //System.out.println("Next turn!! : " + myAgent.getName());
                    if (((AgentWithPosition)myAgent).position.isDead) ;
                        //state = 2;
                    else if (((AgentWithPosition)myAgent).isMotivated()) {
                        decideOnNextStep();
                    } else {
                        ACLMessage deathMsg = new ACLMessage(ACLMessage.INFORM);
                        deathMsg.addReceiver(serverAID);
                        ((AgentWithPosition)myAgent).killYourself(deathMsg);
                    }
                    computationEnded();
                    break;
                case "battle-ended":
                    System.out.println("WE WON!!");
                    state = 2;
                    break;
                case "attack":
                    ((CannonFodder)myAgent).reactToAttack(msg);
                    break;
                case DELETE:
                    myAgent.doDelete();
                    break;
                default:
                    handleMessage(msg);
            }
        } else {
            block();
        }
    }

    protected void computationEnded() {
        ACLMessage m = new ACLMessage(ACLMessage.INFORM);
        m.addReceiver(serverAID);
        m.setConversationId("ended-computation");
        myAgent.send(m);
    }

    @Override
    public boolean done() {
        return state == 2;
    }

    public abstract void handleMessage(ACLMessage msg);

    public abstract void decideOnNextStep();
}
