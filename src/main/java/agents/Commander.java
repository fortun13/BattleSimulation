package main.java.agents;

import edu.wlu.cs.levy.CG.KeySizeException;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import main.java.utils.AgentBuilder;
import main.java.utils.AgentInTree;

import java.util.ArrayList;

/**
 * Class which represents Commanders
 */
public class Commander extends AgentWithPosition {
	
	protected int attractionForce;
	
	public void setup() {
		super.setup();
		
		Object[] p = getArguments();
		
		attractionForce = (int)p[AgentBuilder.ATTRACTION];
	}

	/**
	 * Method is used by Commander to get list of agents that are in his field of view - so he can command them
	 *
	 * @param agent Object of Commander class which represents Commander which is asking for minions
	 * @return list of agents in field of view of Commander
	 */
	public ArrayList<AID> getMinionsWithinRange(Commander agent) {
		ArrayList<AgentInTree> list = new ArrayList<>();
		try {
			world.getAgentsTree()
					.nearestEuclidean(new double[]{agent.currentState.p.getX(), agent.currentState.p.getY()},agent.attractionForce)
					.stream()
					.filter(a -> a.side==agent.currentState.side)
					.forEach(list::add);
		} catch (KeySizeException e) {
			e.printStackTrace();
		}
		if(list.contains(agent.currentState))
			list.remove(agent.currentState);
		ArrayList<AID> ans = new ArrayList<>();
		for (AgentInTree a : list) {
			ans.add(new AID(a.getAgentName(),true));
		}
		return ans;
	}

	protected void sendMessageToEnemy(ACLMessage msgToSend) {
		msgToSend.setConversationId("enemy-dead");
		send(msgToSend);
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
		//simplest version - if i got the message - then i will get hit
		if (currentState.condition <= str) {
			//Inform minions about death
			ArrayList<AID> minions = this.getMinionsWithinRange(this);
			ACLMessage commanderDeadMessage = new ACLMessage(ACLMessage.REQUEST);
			commanderDeadMessage.setConversationId("commander-dead");
			commanderDeadMessage.setContent(this.getName());
			minions.forEach(commanderDeadMessage::addReceiver);
			send(commanderDeadMessage);

			//I am dead
			currentState.condition-=str;
			System.out.println("I'm dead :( " + getLocalName());
			ACLMessage msgAboutDeath = msg.createReply();
			msgAboutDeath.setConversationId("enemy-dead");
			send(msgAboutDeath);
			world.killAgent(this);
		} else {
			// I'm still alive
			currentState.condition = currentState.condition - str;
		}
	}
}
