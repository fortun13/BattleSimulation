package main.java.agents;

import edu.wlu.cs.levy.CG.KeySizeException;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import main.java.gui.BoardPanel;
import main.java.utils.AgentInTree;
import javafx.geometry.Point2D;

import java.util.ArrayList;

/**
 * Created by Fortun on 2014-12-03.
 *
 */
public class CommanderMinionBehaviour extends ReactiveBehaviour {
    private static final int FOLLOWIN = 1;
    private static final int BORED = 0;
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
                ((CannonFodder)myAgent).morale -= 10;
                //myAgent.removeBehaviour(new CommanderMinionBehaviour());
                //myAgent.addBehaviour(new BerserkBehaviour());
                commanderPosX = ((CannonFodder) myAgent).world.returnBoardCenter().getX();
                commanderPosY = ((CannonFodder) myAgent).world.returnBoardCenter().getY();
                commander = null;
                break;
        }
    }

    @Override
    public void decideOnNextStep() {
        CannonFodder agent = (CannonFodder) myAgent;
        if(stance) {
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
                        if(commanderPosX != null && commanderPosY != null){
                            Point2D destination = new Point2D(commanderPosX, commanderPosY);
                            ((CannonFodder) myAgent).goToPoint(destination);
                        }
                        else
                            ((CannonFodder) myAgent).goToPoint(((CannonFodder) myAgent).world.returnBoardCenter());
                    }
                    break;
                case FOLLOWIN:
                    if (agent.enemyInRangeOfAttack(enemyPosition)) {
                        agent.setSpeedVector(0, 0);
                        agent.attack(enemy, enemyPosition);
                    } else {
                        agent.gotoEnemy(enemyPosition);
                        break;
                    }
                    break;
            }
        }
        else {
            if(commanderPosX != null && commanderPosY != null){
                Point2D destination = new Point2D(commanderPosX, commanderPosY);
                ((CannonFodder) myAgent).goToPoint(destination);
            }
            else
                ((CannonFodder) myAgent).goToPoint(((CannonFodder) myAgent).world.returnBoardCenter());
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
