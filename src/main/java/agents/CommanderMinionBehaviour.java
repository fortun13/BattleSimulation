package main.java.agents;

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
    Double commanderPosX = new Double(0);
    Double commanderPosY = new Double(0);

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
                ((CannonFodder)myAgent).position.morale -= 10;
                stance = BERSERK;
                commanderPosX = ((CannonFodder) myAgent).world.returnBoardCenter().getX();
                commanderPosY = ((CannonFodder) myAgent).world.returnBoardCenter().getY();
                ((CannonFodder)myAgent).setCommander(null);
                break;
            case "commander-init":
                ((CannonFodder)myAgent).setCommander(msg.getSender());
                stance = FOLLOWING;
                break;
        }
    }

    @Override
    public void decideOnNextStep() {
        CannonFodder agent = (CannonFodder) myAgent;
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
                            else if (stance == BERSERK)
                                ((CannonFodder) myAgent).goToPoint(((CannonFodder) myAgent).world.returnBoardCenter());
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

    private void doAction(Runnable action) {
        if (enemyPosition.isDead) {
            enemyPosition = null;
            enemy = null;
            state--;
            return ;
        }
        action.run();
    }

    private void gotoCommander() {
        Point2D destination;
        int centerDirection;
        centerDirection = (int)(((CannonFodder)myAgent).world.returnBoardCenter().getX() - commanderPosX);
        switch (((CannonFodder)myAgent).type) {
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
        ((CannonFodder) myAgent).goToPoint(destination);
    }
}
