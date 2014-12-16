package main.java.agents;

import jade.lang.acl.ACLMessage;
import javafx.geometry.Point2D;

/**
 * Created by Fortun on 2014-12-03.
 *
 */
public class CommanderMinionBehaviour extends ReactiveBehaviour {

    boolean stance = false;
    Double commanderPosX = new Double(0);
    Double commanderPosY = new Double(0);

    @Override
    public void handleMessage(ACLMessage msg) {
        switch(msg.getConversationId()) {
            case "stance-fight":
                commanderPosX = Double.parseDouble(msg.getUserDefinedParameter("commanderPosX"));
                commanderPosY = Double.parseDouble(msg.getUserDefinedParameter("commanderPosY"));
                stance = true;
                break;
            case "stance-march":
                commanderPosX = Double.parseDouble(msg.getUserDefinedParameter("commanderPosX"));
                commanderPosY = Double.parseDouble(msg.getUserDefinedParameter("commanderPosY"));
                stance = false;
                break;
            case "commander-dead":
                ((CannonFodder)myAgent).position.morale -= 10;
                myAgent.removeBehaviour(new CommanderMinionBehaviour());
                myAgent.addBehaviour(new BerserkBehaviour());
                commander = null;
                break;
        }
    }

    @Override
    public void decideOnNextStep() {
        if(stance) {
            enemyPosition = ((CannonFodder) myAgent).getNearestEnemy();
            if (enemyPosition == null) {
                if(commanderPosX != null && commanderPosY != null){
                    Point2D destination = new Point2D(commanderPosX, commanderPosY);
                    ((CannonFodder) myAgent).goToPoint(destination);
                }
            }
            else {
                if (((CannonFodder) myAgent).enemyInRangeOfAttack(enemyPosition))
                    doAction(() -> ((CannonFodder) myAgent).attack(enemy, enemyPosition));
                else
                    doAction(() -> ((CannonFodder) myAgent).gotoEnemy(enemyPosition));
            }
        }
        else {
            if(commanderPosX != null && commanderPosY != null){
                Point2D destination = new Point2D(commanderPosX, commanderPosY);
                ((CannonFodder) myAgent).goToPoint(destination);
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
}
