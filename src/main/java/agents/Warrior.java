package main.java.agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 * Created by Marek on 2014-11-11.
 * Represents an Warrior
 */
public class Warrior extends CannonFodder {

    /*public Warrior() {

        super();
    }*/

    public void setup() {
        super.setup();
    }


    protected void takeDown() {

    }

    @Override
    protected void attack(AID enemy) {
        if (Math.random() * 100 <= getAccuracy()) {
            /*ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(new AID(enemy.getAgentName(), false));
            msg.addReplyTo(world.getAID());
            msg.setContent(String.valueOf(getStrength()));
            msg.setConversationId(String.valueOf(Actions.ATTACK));*/

            System.out.println("Attacking enemy: " + enemy.toString());

            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setConversationId("attack");
            String msgContent = getCondition() + ":" + getStrength() + ":" + getSpeed() + ":" + getAccuracy();
            msg.setContent(msgContent);
            msg.addReplyTo(getAID());
            msg.addReceiver(enemy);
            send(msg);
        }
    }

    @Override
    public boolean enemyInRangeOfAttack(World.AgentInTree enemy) {
        return position.pos().distance(enemy.pos()) < 2;
    }

    @Override
    public void reactToAttack(String content) {
        System.out.println("I'm attacked!! " + getName());
        String[] el = content.split(":");
        int cond = Integer.getInteger(el[0]);
        int str = Integer.getInteger(el[1]);
        int spe = Integer.getInteger(el[2]);
        int acc = Integer.getInteger(el[3]);
        //simplest version - if i got the message - then i will get hit
        if (getCondition() <= str) {
            //I am dead
            //TODO should there be method in world, or should we send message to world?
            System.out.println("I'm dead :(");
        } else {
            // I'm still alive
            setCondition(getCondition()-cond);
        }
    }
}
