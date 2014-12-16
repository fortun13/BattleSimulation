package main.java.agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import main.java.utils.AgentInTree;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Marek on 2014-11-11.
 * Represents an Warrior
 */
@SuppressWarnings("UnusedDeclaration")
public class Warrior extends CannonFodder {

    /*public Warrior() {

        super();
    }*/

    public void setup() {
        super.setup();
    }


    /*protected void takeDown() {

    }
    Nie jest wykorzystywana
    */

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
        return position.pos().distance(enemy.pos()) < attackRange;
    }

    @Override
    public void reactToAttack(ACLMessage msg) {
        if (position.isDead) {
            try {
                Clip clip = AudioSystem.getClip();
                File stream = new File("res/die_fast.wav");
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(stream);
                clip.open(audioInputStream);
                clip.start();
            } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
                System.out.println("Nie będzie muzyki");
            }
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
        if (position.condition <= str) {
            //I am dead
            position.condition-=str;
            //ACLMessage toServer = new ACLMessage(ACLMessage.INFORM);
            //toServer.addReceiver(world.server.getAID());
            //toServer.setConversationId("agent-dead");
            //send(toServer);

            killYourself(msg.createReply());
        } else {
            // I'm still alive
            position.condition = position.condition-str;
        }
    }
}
