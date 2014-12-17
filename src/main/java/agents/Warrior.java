package main.java.agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import main.java.utils.AgentInTree;

/**
 * Created by Marek on 2014-11-11.
 * Represents a Warrior
 */
@SuppressWarnings("UnusedDeclaration")
/**
 * Class which represents Warriors
 */
public class Warrior extends AgentWithPosition {
    public void setup() {
        super.setup();
    }

    @Override
    protected void attack(AID enemy, AgentInTree currentState) {
        if (Math.random() * 100 <= accuracy) {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setConversationId("attack");
            String msgContent = currentState.condition + ":" + strength + ":" + speed + ":" + accuracy;
            msg.setContent(msgContent);
            msg.addReplyTo(getAID());
            msg.addReceiver(enemy);
            msg.setSender(getAID());
            send(msg);
        }
    }

    @Override
    protected void killYourself(ACLMessage msgToSend) {
        System.out.println("I'm dead :( " + getLocalName());
        sendMessageToEnemy(msgToSend);
        world.killAgent(this);
    }

    /**
     * sends message to enemy about his death
     * @param msgToSend message to enemy
     */
    protected void sendMessageToEnemy(ACLMessage msgToSend) {
        msgToSend.setConversationId("enemy-dead");
        send(msgToSend);
    }

    @Override
    public boolean enemyInRangeOfAttack(AgentInTree enemy) {
        return currentState.pos().distance(enemy.pos()) < attackRange;
    }

    @Override
    public void reactToAttack(ACLMessage msg) {
        if (currentState.isDead) {
            sendMessageToEnemy(msg.createReply());
            return ;
        }
        String content = msg.getContent();
        String[] el = content.split(":");
        int cond = Integer.valueOf(el[0]);
        int str = Integer.valueOf(el[1]);
        int spe = Integer.valueOf(el[2]);
        int acc = Integer.valueOf(el[3]);
        if (currentState.condition <= str) {
            currentState.condition-=str;
            ACLMessage toCommander = new ACLMessage(ACLMessage.INFORM);
            toCommander.addReceiver(this.commander);
            toCommander.setConversationId("minion-dead");
            send(toCommander);
            killYourself(msg.createReply());
        } else {
            // I'm still alive
            currentState.condition = currentState.condition-str;
        }
    }
}
