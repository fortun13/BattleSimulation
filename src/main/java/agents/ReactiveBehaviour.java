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
    private static final int WAITING = 3;
    protected int state = 0;
    protected AgentInTree enemyPosition;
    protected AID enemy;

    /**
     * main method of behaviour, responsible (in our case) for processing basic messages
     */
    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            // probably can do it with msg.getPrformative and some ACL static fields, but for now let it be string
            switch (msg.getConversationId()) {
                case "enemy-dead":
                    enemy = null;
                    enemyPosition = null;
                    break;
                case "new-turn":
                    AgentWithPosition agentWithPosition = (AgentWithPosition) myAgent;

                    if (agentWithPosition.currentState.isDead) ;
                        //state = 2;
                    else if (agentWithPosition.isMotivated()) {
                        decideOnNextStep();
                    } else {
                        agentWithPosition.killYourself(msg.createReply());
                    }
                    computationEnded(msg);
                    break;
                case "battle-ended-victory":
                    System.out.println("WE'VE WON!");
                    state = WAITING;
                    break;
                case "battle-ended-loss":
                    System.out.println("WE'VE LOST...");
                    state = WAITING;
                    break;
                case "battle-ended-draw":
                    System.out.println("DRAW");
                    state = WAITING;
                    break;
                case "attack":
                    ((AgentWithPosition) myAgent).reactToAttack(msg);
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

    /**
     * method sends information to server that agent ended his "thinking" for this turn
     * @param msg message from server about "next turn" for which we are replaying
     */
    protected void computationEnded(ACLMessage msg) {
        ACLMessage m = msg.createReply();
        m.setConversationId("ended-computation");
        myAgent.send(m);
    }

    @Override
    public boolean done() {
        return state == 2;
    }

    /**
     * method which (when overriden) will allow behaviour to react for more variety of messages
     * @param msg message to which we want to react
     */
    public abstract void handleMessage(ACLMessage msg);

    /**
     * method which is responsible for computing agent behaviour for next step
     */
    public abstract void decideOnNextStep();
}
