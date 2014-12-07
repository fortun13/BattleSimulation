package main.java.agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 * Created by Jakub Fortunka on 18.11.14.
 * Behaviour presented in a frenzy of battle
 */
public class BerserkBehaviour extends ReactiveBehaviour {

    public BerserkBehaviour(AID serverAID) {
        super(serverAID);
    }

    @Override
    public void handleMessage(ACLMessage msg) {
        switch (msg.getConversationId()) {
            case "commander-init":
                //TODO probably will have to check if this turn message was send to server (boolean?)
                myAgent.addBehaviour(new CommanderMinionBehaviour(serverAID, new AID(msg.getContent(), false)));
                state = 2;
                break;
        }
    }

    @Override
    public void decideOnNextStep() {
        if (((CannonFodder)myAgent).condition <= 0) {
            return ;
        }
        switch(state) {
            case 0:
                //findEnemy, if enemyFound goto state 2
                enemyPosition = ((AgentWithPosition)myAgent).getNearestEnemy();
                if (enemyPosition != null) {
                    //System.out.println("Found enemy!" + myAgent.getName());
                    enemy = new AID(enemyPosition.getAgentName(),true);
                    ((AgentWithPosition)myAgent).gotoEnemy(enemyPosition);
                    state++;
                }
                else {
                    //moveSomewhere
                    ((AgentWithPosition)myAgent).keepPosition();
                }
                break;
            case 1:
                // I assume that "world" will kill agent, so, when enemy will die, Agent enemy will become null (not sure if it's good thinking though)
                if (enemy == null) {
                    enemyPosition = ((AgentWithPosition)myAgent).getNearestEnemy();
                    if (enemyPosition == null) {
                        state--;
                        return ;
                    } else {
                        enemy = new AID(enemyPosition.getAgentName(),true);
                    }
                }
                if (((AgentWithPosition)myAgent).enemyInRangeOfAttack(enemyPosition))
                    doAction(() -> ((CannonFodder) myAgent).attack(enemy, enemyPosition));
                else
                    doAction(() -> ((CannonFodder)myAgent).gotoEnemy(enemyPosition));
                break;
        }
    }

    private void doAction(Runnable action) {
        if (enemyPosition.isDead) {
            enemyPosition = null;
            enemy = null;
            state--;
            return ;
        }
        action.run();
    }

}
