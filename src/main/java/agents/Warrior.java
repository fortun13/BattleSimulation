package main.java.agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import main.java.utils.AgentInTree;

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

            //System.out.println(getLocalName() + " Attacking enemy: " + enemy.toString());

            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setConversationId("attack");
            String msgContent = getCondition() + ":" + getStrength() + ":" + getSpeed() + ":" + getAccuracy();
            msg.setContent(msgContent);
            msg.addReplyTo(getAID());
            msg.addReceiver(enemy);
            msg.setSender(getAID());
            send(msg);
        }
    }

    @Override
    public boolean isMotivated() {
        //return true;
        int [] count;
        switch (position.side) {
            case Blues:
                count = world.countFriendFoe(this);
                break;
            default:
                count = world.countFriendFoe(this);
                break;
        }
        //System.out.println("Friends: " + count[0] + " Enemies: " + count[1]);
        if (count[1] == 0)
            return true;
        double ratio = ((double)count[0])/((double)count[1]);
        //System.out.println("Ratio: " + ratio);
        if (ratio < psychologicalResistance && ratio < previousRatio) {
            morale -= (1/ratio +2);

        }
        if (ratio >= 1 && morale<50)
            morale += ratio;

        previousRatio = ratio;
        //System.out.println(getLocalName() + " Morale: " + morale);
        return morale > 0;
    }

    @Override
    protected void killYourself(ACLMessage msgToSend) {
        System.out.println("I'm dead :( " + getLocalName());
        sendMessageToEnemy(msgToSend);
        //msgToSend.setConversationId("enemy-dead");
        //msgToSend.addReceiver(world.server.getAID());
        //send(msgToSend);
        world.killAgent(this);
    }

    protected void sendMessageToEnemy(ACLMessage msgToSend) {
        msgToSend.setConversationId("enemy-dead");
        send(msgToSend);
    }

    @Override
    public boolean enemyInRangeOfAttack(AgentInTree enemy) {
        return position.pos().distance(enemy.pos()) < 2;
    }

    @Override
    public void reactToAttack(ACLMessage msg) {
        if (position.isDead) {
            sendMessageToEnemy(msg.createReply());
            return ;
        }
        //System.out.println("I'm attacked!! " + getName());
        String content = msg.getContent();
        String[] el = content.split(":");
        int cond = Integer.valueOf(el[0]);
        int str = Integer.valueOf(el[1]);
        int spe = Integer.valueOf(el[2]);
        int acc = Integer.valueOf(el[3]);
        //simplest version - if i got the message - then i will get hit
        if (condition <= str) {
            //I am dead
            condition-=str;
            //ACLMessage toServer = new ACLMessage(ACLMessage.INFORM);
            //toServer.addReceiver(world.server.getAID());
            //toServer.setConversationId("agent-dead");
            //send(toServer);

            killYourself(msg.createReply());
        } else {
            // I'm still alive
            condition = condition-str;
        }
    }
}
