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
    private static final int WAITN = 3;
    protected int state = 0;
    protected AgentInTree enemyPosition;
    protected AID enemy;

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

                    if (agentWithPosition.position.isDead) ;
                        //state = 2;
                    else if (agentWithPosition.isMotivated()) {
                        decideOnNextStep();
                    } else {
                        agentWithPosition.killYourself(msg.createReply());
                    }
                    computationEnded(msg);
                    break;
                case "battle-ended":
                    System.out.println("WE WON!!");
                    state = WAITN;
                    break;
                case "attack":
                    ((CannonFodder) myAgent).reactToAttack(msg);
                    break;
                case DELETE:
                    myAgent.doDelete();
                    break;
                case "commander-init":
                    /*
                    Element debugujący
                    Wychodzi na to że commander wysyła wiadomość do samego siebie
                     Nie było tego widać ponieważ tylko Berserk odpowiadał na ten typ wiadomości
                     */
                    System.out.println("You have my soul " + myAgent.getLocalName());
                    myAgent.removeBehaviour(new BerserkBehaviour());
                    myAgent.addBehaviour(new CommanderMinionBehaviour());
                    state = 2;
                    break;
                default:
                    handleMessage(msg);
            }
        } else {
            block();
        }
    }

    protected void computationEnded(ACLMessage msg) {
        ACLMessage m = msg.createReply();
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
