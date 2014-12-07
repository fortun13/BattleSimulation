package main.java.agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import main.java.utils.AgentInTree;

public class Commander extends CannonFodder {
	
	protected int attractionForce;
	
	public void setup() {
		super.setup();
		
		Object[] p = getArguments();
		
		attractionForce = (int)p[8];
	}

    @Override
    protected void attack(AID enemy, AgentInTree position) {
        if (Math.random() * 100 <= accuracy) {
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
		if (condition <= str) {
			//I am dead
			condition-=str;
			System.out.println("I'm dead :( " + getLocalName());
			ACLMessage msgAboutDeath = msg.createReply();
			msgAboutDeath.setConversationId("enemy-dead");
			send(msgAboutDeath);
			world.killAgent(this);
		} else {
			// I'm still alive
			condition = condition-str;
		}
		
	}

    @Override
    public boolean isMotivated() {
        return false;
    }

    @Override
    protected void killYourself(ACLMessage msgToSend) {

    }

}
