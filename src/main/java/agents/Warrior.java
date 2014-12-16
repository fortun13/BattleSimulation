package main.java.agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import main.java.utils.AgentInTree;
import main.java.agents.World.AgentType;

/**
 * Created by Marek on 2014-11-11.
 * Represents a Warrior
 */
@SuppressWarnings("UnusedDeclaration")
public class Warrior extends CannonFodder {
    public void setup() {
        super.setup();
        this.getPosition().type = AgentType.WARRIOR;
    }

    @Override
    protected void attack(AID enemy, AgentInTree position) {
        if (Math.random() * 100 <= accuracy) {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setConversationId("attack");
            String msgContent = position.condition + ":" + strength + ":" + speed + ":" + accuracy;
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

    protected void sendMessageToEnemy(ACLMessage msgToSend) {
        msgToSend.setConversationId("enemy-dead");
        send(msgToSend);
    }

    @Override
    public boolean enemyInRangeOfAttack(AgentInTree enemy) {
        return position.pos().distance(enemy.pos()) < attackRange;
    }

    @Override
    public void reactToAttack(ACLMessage msg) {
        if (position.isDead) {
            /*try {
                Clip clip = AudioSystem.getClip();
                File stream = new File("res/die_fast.wav");
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(stream);
                clip.open(audioInputStream);
                clip.start();
            } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
                System.out.println("Nie będzie muzyki");
            }*/
            sendMessageToEnemy(msg.createReply());
            return ;
        }
        String content = msg.getContent();
        String[] el = content.split(":");
        int cond = Integer.valueOf(el[0]);
        int str = Integer.valueOf(el[1]);
        int spe = Integer.valueOf(el[2]);
        int acc = Integer.valueOf(el[3]);
        if (position.condition <= str) {
            position.condition-=str;
            ACLMessage toCommander = new ACLMessage(ACLMessage.INFORM);
            toCommander.addReceiver(this.commander);
            toCommander.setConversationId("minion-dead");
            send(toCommander);
            killYourself(msg.createReply());
        } else {
            // I'm still alive
            position.condition = position.condition-str;
        }
    }
}
