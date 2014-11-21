package main.java.agents;

import jade.core.AID;

/**
 * Created by Jakub Fortunka on 18.11.14.
 * Behaviour presented in a frenzy of battle
 */
public class BerserkBehaviour extends ReactiveBehaviour {

    @Override
    public void decideOnNextStep() {
        switch(state) {
            case 0:
                //findEnemy, if enemyFound goto state 2
                enemyPosition = ((AgentWithPosition)myAgent).getNearestEnemy();
                if (enemyPosition != null) {
                    enemy = new AID(enemyPosition.getAgentName(),true);
                    ((AgentWithPosition)myAgent).gotoEnemy(enemyPosition);
                    state++;
                }
                else {
                    //moveSomewhere
                    System.out.println("Staying in place (for now...)");
                }
                break;
            case 1:
                // I assume that "world" will kill agent, so, when enemy will die, Agent enemy will become null (not sure if it's good thinking though)
                if (enemy == null) {
                    enemyPosition = ((AgentWithPosition)myAgent).getNearestEnemy();
                    if (enemyPosition == null) {
                        state--;
                        break;
                    }
                }
                if (((AgentWithPosition)myAgent).enemyInRangeOfAttack(enemyPosition))
                    ((CannonFodder)myAgent).attack(enemy);
                else
                    ((CannonFodder)myAgent).gotoEnemy(enemyPosition);
//                agent.attack(enemy);
                break;
        }
    }

}
