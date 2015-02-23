package main.java.agents;

import jade.lang.acl.ACLMessage;

/**
 * Created by Jakub Fortunka on 19.11.14.
 *
 */
@SuppressWarnings("unused")
/**
 * Class representing Archers
 */
public class Archer extends AgentWithPosition {

    public void setup() {
        super.setup();
    }

    protected void takeDown() {

    }

    /**
     * {@inheritDoc}
     * @param enemy AID of enemy
     * @param currentState current state of agent (position etc.)
     */
//    @Override
//    protected void attack(AID enemy, AgentInTree currentState) {
//        if (Math.random() * 100 <= (stats.accuracy - currentState.p.distance(currentState.p)*2)) {
//            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
//            msg.setConversationId("attack");
//            String msgContent = currentState.condition + ":" + stats.strength + ":" + stats.speed + ":" + stats.accuracy;
//            msg.setContent(msgContent);
//            msg.addReplyTo(getAID());
//            msg.addReceiver(enemy);
//            msg.setSender(getAID());
//            send(msg);
//        }
//    }
    /**
     * {@inheritDoc}
     * @param msg message from enemy with his parameters
     */
    @Override
    public void reactToAttack(ACLMessage msg) {
        String content = msg.getContent();
        System.out.println("Arch Msg: " + content);
        String[] el = content.split(":");
        int cond = Integer.valueOf(el[0]);
        int str = Integer.valueOf(el[1]);
        int spe = Integer.valueOf(el[2]);
        int acc = Integer.valueOf(el[3]);
        //simplest version - if i got the message - then i will get hit
        if (currentState.condition <= str) {
            //I am dead
            currentState.condition-=str;
            killYourself(msg.createReply());
        } else {
            // I'm still alive
            currentState.condition = currentState.condition-str;
        }
    }
}
