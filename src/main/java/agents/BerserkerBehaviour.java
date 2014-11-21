package main.java.agents;

/**
 * Created by Jakub Fortunka on 18.11.14.
 */
public class BerserkerBehaviour extends ReactiveBehaviour {

    @Override
    public void decideOnNextStep() {
        switch(state) {
            case 0:
                //findEnemy, if enemyFound goto state 2
                enemy = ((CannonFodder)myAgent).getNearestEnemy();
                if (enemy != null) {
                    ((CannonFodder)myAgent).gotoEnemy(enemy);
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
                    enemy = ((CannonFodder)myAgent).getNearestEnemy();
                    if (enemy == null) {
                        state--;
                        break;
                    }
                }
                if (((CannonFodder)myAgent).enemyInRangeOfAttack(enemy))
                    ((CannonFodder)myAgent).attack(enemy);
                else
                    ((CannonFodder)myAgent).gotoEnemy(enemy);
//                agent.attack(enemy);
                break;
        }
    }

}
