package main.java.agents;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;

/**
 * Created by fortun on 03.12.14.
 */
public class CommanderBehaviour extends Behaviour {

    private ArrayList<AID> minions = new ArrayList<>();
    private AID serverAID;

    protected World.AgentInTree enemyPosition;

    int state = 0;

    public CommanderBehaviour(AID serverAID) {
        this.serverAID = serverAID;
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            switch (msg.getConversationId()) {
                case "new-turn":
                    decideOnAction();
                    computationEnded();
                    break;
                case "attack":
                    ((Commander)myAgent).reactToAttack(msg);
                    break;
                case "enemy-dead":
                    enemyPosition = null;
                    break;
            }

        } else {
            block();
        }
    }

    protected void computationEnded() {
        ACLMessage m = new ACLMessage(ACLMessage.INFORM);
        //TODO it's not good practice - i just want to test if this will work
        m.addReceiver(serverAID);
        m.setConversationId("ended-computation");
        myAgent.send(m);
    }

    protected void decideOnAction() {
        switch (state) {
            case 0:
                //TODO - get some limit for controlled minions
                minions = ((AgentWithPosition) myAgent).getMinionsWithinRange(((Commander)myAgent).position.p,((Commander) myAgent).attractionForce, ((Commander)myAgent).side);
                ACLMessage commanderAddMessage = new ACLMessage(ACLMessage.REQUEST);
                commanderAddMessage.setConversationId("commander-init");
                commanderAddMessage.setContent(myAgent.getName());
                minions.forEach(commanderAddMessage::addReceiver);
                state++;
                break;
            case 1:
                //TODO for now - basic, find nearest enemy and send minions to kill
                enemyPosition = ((CannonFodder)myAgent).getNearestEnemy();
                if (enemyPosition != null)
                    state++;
                break;
            case 2:
                // enemyPosition can be nulled if enemy was killed in the meantime
                if (enemyPosition != null) {
                    ACLMessage sendToKill = new ACLMessage(ACLMessage.REQUEST);
                    minions.forEach(sendToKill::addReceiver);
                    sendToKill.setConversationId("kill-enemy");
                    sendToKill.setContent(enemyPosition.getAgentName());
                    break;
                } else
                    state--;
                break;

        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
