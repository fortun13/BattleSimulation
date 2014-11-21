package main.java.agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
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

    @Override
    protected ACLMessage attack(World.AgentInTree enemy) {
        //simple formula for now
        // can actually use for example speed to use more properties
        // i.e. - int speedPenalty = attacked.getSpeed() - attacker.getSpeed();
        //          if (speedPenalty > 0)
        //              Math.random()*100 > (attacker.getAccuracy() - speedPenalty)
        if (Math.random() * 100 <= getAccuracy()) {
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(new AID(enemy.getAgentName(), false));
            msg.addReplyTo(world.getAID());
            msg.setContent(String.valueOf(getStrength()));
            msg.setConversationId(String.valueOf(Actions.ATTACK));

            return msg;
        }
        return null;
    }


}
