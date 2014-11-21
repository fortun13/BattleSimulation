package main.java.agents;

import jade.core.AID;

/**
 * Created by Jakub Fortunka on 18.11.14.
 */
public class BerserkerBehaviour extends ReactiveBehaviour {

    @Override
    public void decideOnNextStep() {
        switch(state) {
            case 0:
                //findEnemy, if enemyFound goto state 2
                enemyPosition = ((CannonFodder)myAgent).getNearestEnemy();

                //enemy = ((CannonFodder)myAgent).getNearestEnemy();


                if (enemyPosition != null) {
                    enemy = new AID(enemyPosition.getAgentName(),true);
                    ((CannonFodder)myAgent).gotoEnemy(enemyPosition);
                    state++;
                }
                else {
                    //moveSomewhere
                    ((CannonFodder)myAgent).moveSomewhere();
                }
                break;
            case 1:
                // I assume that "world" will kill agent, so, when enemy will die, Agent enemy will become null (not sure if it's good thinking though)
                if (enemy == null) {
                    enemyPosition = ((CannonFodder)myAgent).getNearestEnemy();
                    if (enemyPosition == null) {
                        state--;
                        break;
                    }
                }
                if (((CannonFodder)myAgent).enemyInRangeOfAttack(enemyPosition))
                    ((CannonFodder)myAgent).attack(enemy,enemyPosition);
                else
                    ((CannonFodder)myAgent).gotoEnemy(enemyPosition);
//                agent.attack(enemy);
                break;
        }
    }

}
