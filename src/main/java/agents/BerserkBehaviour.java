package main.java.agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 * Created by Jakub Fortunka on 18.11.14.
 * Behaviour presented in a frenzy of battle
 */
public class BerserkBehaviour extends ReactiveBehaviour {
    private static final int FOLLOWIN = 1;
    private static final int BORED = 0;

    @Override
    public void handleMessage(ACLMessage msg) {
        switch (msg.getConversationId()) {
            case "commander-init":
                //TODO probably will have to check if this turn message was send to server (boolean?)
                myAgent.removeBehaviour(new BerserkBehaviour());
                myAgent.addBehaviour(new CommanderMinionBehaviour());
                state = 2;
                break;
        }
    }

    @Override
    public void decideOnNextStep() {
        CannonFodder agent = (CannonFodder) myAgent;

        if (agent.position.condition <= 0) {
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
                    state = FOLLOWIN;
                }
                else {
                    //moveSomewhere
                    agent.keepPosition();
                }
                break;
            case FOLLOWIN:
                if (agent.enemyInRangeOfAttack(enemyPosition)) {
                    agent.setSpeedVector(0, 0);
                    agent.attack(enemy, enemyPosition);
                }
                else
                    agent.gotoEnemy(enemyPosition);
                break;
        }
    }
}
