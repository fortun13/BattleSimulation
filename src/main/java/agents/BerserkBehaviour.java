package main.java.agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 * Created by Jakub Fortunka on 18.11.14.
 * Behaviour presented in a frenzy of battle
 */
public class BerserkBehaviour extends ReactiveBehaviour {
    private static final int FOLLOWING = 1;
    private static final int BORED = 0;

    /**
     * {@inheritDoc}
     * @param msg message to which we want to react
     */
    @Override
    public void handleMessage(ACLMessage msg) {
        switch (msg.getConversationId()) {
            case "commander-init":
                //TODO probably will have to check if this turn message was send to server (boolean?)
                myAgent.removeBehaviour(new BerserkBehaviour());
                myAgent.addBehaviour(new CommanderMinionBehaviour());
                ((AgentWithPosition)myAgent).setCommander(msg.getSender());
                state = 2;
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void decideOnNextStep() {
        AgentWithPosition agent = (AgentWithPosition) myAgent;
        if (agent.currentState.condition <= 0) {
            return ;
        }
        if (enemyPosition == null || enemyPosition.isDead) {
            state = BORED;
        }
        switch(state) {
            case BORED:
                enemyPosition = agent.getNearestEnemy();
                if (enemyPosition != null) {
                    enemy = new AID(enemyPosition.getAgentName(),true);
                    agent.gotoEnemy(enemyPosition);
                    state = FOLLOWING;
                }
                else {
                    agent.goToPoint(agent.world.returnBoardCenter());
                }
                break;
            case FOLLOWING:
                if (agent.enemyInRangeOfAttack(enemyPosition)) {
                    agent.setSpeedVector(0, 0);
                    agent.attack(enemy, enemyPosition);
                } else {
                    agent.gotoEnemy(enemyPosition);
                }
                break;
        }
    }
}
