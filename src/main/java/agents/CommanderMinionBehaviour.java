package agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import javafx.geometry.Point2D;

/**
 * Created by Fortun on 2014-12-03.
 *
 */
public class CommanderMinionBehaviour extends ReactiveBehaviour {
    //Stałe opisujące bieżące działania
    private static final int ATTACKING = 1;
    private static final int SEARCHING = 0;
    //Stałe opisujące postawę żołnierza
    private static final int BERSERK = 2;
    private static final int FIGHTING = 1;
    private static final int FOLLOWING = 0;

    int stance = FOLLOWING;
    Double commanderPosX = (double) 0;
    Double commanderPosY = (double) 0;

    @Override
    public void handleMessage(ACLMessage msg) {
        switch(msg.getConversationId()) {
            case "stance-fight":
                commanderPosX = Double.parseDouble(msg.getUserDefinedParameter("commanderPosX"));
                commanderPosY = Double.parseDouble(msg.getUserDefinedParameter("commanderPosY"));
                stance = FIGHTING;
                break;
            case "stance-march":
                commanderPosX = Double.parseDouble(msg.getUserDefinedParameter("commanderPosX"));
                commanderPosY = Double.parseDouble(msg.getUserDefinedParameter("commanderPosY"));
                stance = FOLLOWING;
                break;
            case "commander-dead":
                ((AgentWithPosition)myAgent).currentState.morale -= 10;
                stance = BERSERK;
                commanderPosX = ((AgentWithPosition) myAgent).world.returnBoardCenter().getX();
                commanderPosY = ((AgentWithPosition) myAgent).world.returnBoardCenter().getY();
                ((AgentWithPosition)myAgent).setCommander(null);
                break;
            case "commander-init":
                ((AgentWithPosition)myAgent).setCommander(msg.getSender());
                stance = FOLLOWING;
                break;
        }
    }

    @Override
    public void decideOnNextStep() {
        AgentWithPosition agent = (AgentWithPosition) myAgent;
        switch (stance) {
            case FOLLOWING:
                gotoCommander();
                break;
            case FIGHTING:
            case BERSERK:
                if (enemyPosition == null || enemyPosition.isDead) {
                    state = SEARCHING;
                }
                switch (state) {
                    case SEARCHING:
                        enemyPosition = agent.getNearestEnemy();
                        if (enemyPosition != null) {
                            enemy = new AID(enemyPosition.getAgentName(), true);
                            agent.gotoEnemy(enemyPosition);
                            state = ATTACKING;
                        } else {
                            if(stance == FIGHTING){
                                gotoCommander();
                            }
                            else agent.goToPoint(agent.world.returnBoardCenter());
                        }
                        break;
                    case ATTACKING:
                        if (agent.enemyInRangeOfAttack(enemyPosition)) {
                            agent.setSpeedVector(0, 0);
                            agent.attack(enemy, enemyPosition);
                        }
                        else
                            //agent.gotoEnemy(enemyPosition);
                            gotoCommander();
                        break;
                }
        }
    }

    private void gotoCommander() {
        Point2D destination;
        int centerDirection;
        centerDirection = (int)(((AgentWithPosition)myAgent).world.returnBoardCenter().getX() - commanderPosX);
        switch (((AgentWithPosition)myAgent).getCurrentState().type) {
            case WARRIOR:
                if(centerDirection >= 0)
                    destination = new Point2D(commanderPosX + 20, commanderPosY);
                else
                    destination = new Point2D(commanderPosX - 20, commanderPosY);
                break;
            case ARCHER:
                if(centerDirection >= 0)
                    destination = new Point2D(commanderPosX - 20, commanderPosY);
                else
                    destination = new Point2D(commanderPosX + 20, commanderPosY);
                break;
            default:
                destination = new Point2D(commanderPosX, commanderPosY);
                break;
        }
        ((AgentWithPosition) myAgent).goToPoint(destination);
    }
}
