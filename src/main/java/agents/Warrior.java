package main.java.agents;

import jade.lang.acl.ACLMessage;

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
