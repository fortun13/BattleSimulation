package main.java.agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import javafx.geometry.Point2D;

/**
 * Created by Fortun on 2014-12-03.
 *
 */
public class CommanderMinionBehaviour extends ReactiveBehaviour {

    AID commander;
    boolean stance = false;
    double speedVec;

    @Override
    public void handleMessage(ACLMessage msg) {
        switch(msg.getConversationId()) {
            case "stance-fight":
                stance = true;
                //TODO - get enemyPosition somehow...
                break;
            case "stance-march":
                stance = false;
                speedVec = Double.parseDouble(msg.getUserDefinedParameter("speedVecXVal"));
                break;
        }
    }

    @Override
    public void decideOnNextStep() {
        CannonFodder agent = (CannonFodder) myAgent;
        if(stance) {
            enemyPosition = ((CannonFodder) myAgent).getNearestEnemy();
            if (((CannonFodder) myAgent).enemyInRangeOfAttack(enemyPosition))
                doAction(() -> ((CannonFodder) myAgent).attack(enemy, enemyPosition));
            else
                doAction(() -> ((CannonFodder) myAgent).gotoEnemy(enemyPosition));
        }
        else {
            Point2D thisPosition = agent.getPosition().pos();
            Point2D destination = new Point2D(thisPosition.getX() + speedVec, thisPosition.getY());
            agent.world.moveAgent(agent, destination);
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
