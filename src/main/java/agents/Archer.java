package main.java.agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import main.java.utils.AgentInTree;

/**
 * Created by Jakub Fortunka on 19.11.14.
 */
public class Archer extends CannonFodder {

    private int attackRange;


    public void setup() {

        Object[] parameters = getArguments();

        addBehaviour((ReactiveBehaviour) parameters[0]);
        this.condition = (int) parameters[1];
        this.strength = (int) parameters[2];
        this.speed = (int) parameters[3];
        this.accuracy  = (int) parameters[4];
        this.world = (World) parameters[5];
        this.position = (AgentInTree) parameters[6];
        this.attackRange = (int) parameters[7];
    }

    protected void takeDown() {

    }

    @Override
    protected void attack(AID enemy, AgentInTree position) {
        if (Math.random() * 100 <= (accuracy - position.p.distance(position.p)*2)) {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setConversationId("attack");
            String msgContent = condition + ":" + strength + ":" + speed + ":" + accuracy;
            msg.setContent(msgContent);
            msg.addReplyTo(getAID());
            msg.addReceiver(enemy);
            msg.setSender(getAID());
            send(msg);
        }
    }


    public int getAttackRange() {
        return attackRange;
    }

    @Override
    public boolean enemyInRangeOfAttack(AgentInTree enemy) {
        return getPosition().pos().distance(enemy.pos()) < (2+getAttackRange());
    }

    @Override
    public void reactToAttack(ACLMessage msg) {
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
            killYourself(msg.createReply());
        } else {
            // I'm still alive
            condition = condition-str;
        }
    }

    @Override
    protected void killYourself(ACLMessage msgToSend) {
        System.out.println("I'm dead :( " + getLocalName());
        msgToSend.setConversationId("enemy-dead");
        send(msgToSend);
        world.killAgent(this);
    }
}
