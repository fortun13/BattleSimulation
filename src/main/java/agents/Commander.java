package main.java.agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import main.java.utils.AgentInTree;

import java.util.ArrayList;

public class Commander extends CannonFodder {
	
	protected int attractionForce;
	
	public void setup() {
		super.setup();
		
		Object[] p = getArguments();
		
		attractionForce = (int)p[7];
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
	protected boolean enemyInRangeOfAttack(AgentInTree enemy) {
		return (position.p.distance(enemy.p) < 2);
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
		if (position.condition <= str) {
			//Inform minions about death
			ArrayList<AID> minions = this.getMinionsWithinRange(this);
			ACLMessage commanderDeadMessage = new ACLMessage(ACLMessage.REQUEST);
			commanderDeadMessage.setConversationId("commander-dead");
			commanderDeadMessage.setContent(this.getName());
			minions.forEach(commanderDeadMessage::addReceiver);
			send(commanderDeadMessage);

			//I am dead
			position.condition-=str;
			System.out.println("I'm dead :( " + getLocalName());
			ACLMessage msgAboutDeath = msg.createReply();
			msgAboutDeath.setConversationId("enemy-dead");
			send(msgAboutDeath);
			world.killAgent(this);
		} else {
			// I'm still alive
			position.condition = position.condition-str;
		}
		
	}

}
