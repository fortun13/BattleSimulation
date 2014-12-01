package main.java.agents;

import jade.core.AID;

/**
 * Created by Jakub Fortunka on 18.11.14.
 * Behaviour presented in a frenzy of battle
 */
public class BerserkBehaviour extends ReactiveBehaviour {

    public BerserkBehaviour(AID serverAID) {
        super(serverAID);
    }

    private enum ActionType { Attack, GoTo };

    @Override
    public void decideOnNextStep() {
        if (((CannonFodder)myAgent).condition <= 0) {
            computationEnded();
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
                    } else {
                        enemy = new AID(enemyPosition.getAgentName(),true);
                    }
                }
                if (((AgentWithPosition)myAgent).enemyInRangeOfAttack(enemyPosition))
                    doAction(ActionType.Attack);
                else
                    doAction(ActionType.GoTo);

//                agent.attack(enemy);
                break;
        }
    }

    private void doAction(ActionType action) {
        if (enemyPosition.isDead) {
            enemyPosition = null;
            enemy = null;
            state--;
            return ;
        }
        switch(action) {
            case Attack:
                ((CannonFodder) myAgent).attack(enemy);
                break;
            case GoTo:
                ((CannonFodder)myAgent).gotoEnemy(enemyPosition);
                break;
        }
    }

}
