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
                enemy = agent.getNearestEnemy();
                if (enemy != null) {
                    agent.gotoEnemy(enemy);
                    state++;
                }
                else {
                    //moveSomewhere
//                    agent.moveSomewhere();
                }
                break;
            case 1:
                // I assume that "world" will kill agent, so, when enemy will die, Agent enemy will become null (not sure if it's good thinking though)
                if (enemy == null) {
                    enemy = agent.getNearestEnemy();
                    if (enemy == null) {
                        state--;
                        break;
                    }
                }
                if (agent.enemyInRangeOfAttack(enemy))
                    agent.attack(enemy);
                else
                    agent.gotoEnemy(enemy);
//                agent.attack(enemy);
                break;
        }
    }

}
