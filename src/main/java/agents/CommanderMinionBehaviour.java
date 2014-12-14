package main.java.agents;

import edu.wlu.cs.levy.CG.KeySizeException;
import jade.lang.acl.ACLMessage;
import main.java.gui.BoardPanel;
import main.java.utils.AgentInTree;
import javafx.geometry.Point2D;

/**
 * Created by Fortun on 2014-12-03.
 *
 */
public class CommanderMinionBehaviour extends ReactiveBehaviour {

    boolean stance = false;
    Double commanderPosX = new Double(0);
    Double commanderPosY = new Double(0);
    double speedVec;

    @Override
    public void handleMessage(ACLMessage msg) {
        switch(msg.getConversationId()) {
            case "stance-fight":
                //System.out.println("biję " + myAgent.getLocalName());
                commanderPosX = Double.parseDouble(msg.getUserDefinedParameter("commanderPosX"));
                commanderPosY = Double.parseDouble(msg.getUserDefinedParameter("commanderPosY"));
                stance = true;
                break;
            case "stance-march":
                //System.out.println("idę " + myAgent.getLocalName());
                commanderPosX = Double.parseDouble(msg.getUserDefinedParameter("commanderPosX"));
                commanderPosY = Double.parseDouble(msg.getUserDefinedParameter("commanderPosY"));
                stance = false;
                speedVec = Double.parseDouble(msg.getUserDefinedParameter("speedVecXVal"));
                break;
            case "commander-dead":
                //System.out.println("ojej " + myAgent.getLocalName());
                ((CannonFodder)myAgent).morale -= 10;
                myAgent.removeBehaviour(new CommanderMinionBehaviour());
                myAgent.addBehaviour(new BerserkBehaviour());
                commander = null;
                break;
        }
    }

    @Override
    public void decideOnNextStep() {
        //System.out.println("Działam jako minion " + myAgent.getLocalName());
        CannonFodder agent = (CannonFodder) myAgent;
        if(stance) {
            enemyPosition = ((CannonFodder) myAgent).getNearestEnemy();
            if (enemyPosition == null) {
                /*Point2D thisPosition = agent.getPosition().pos();
                Point2D destination = new Point2D(thisPosition.getX() + speedVec, thisPosition.getY());
                agent.world.moveAgent(agent, destination);*/
                if(commanderPosX != null && commanderPosY != null){
                    double[] key = {commanderPosX, commanderPosY};
                    try {
                        AgentInTree commanderAIT = agent.world.getAgentsTree().search(key);
                        if(commanderAIT != null)
                            agent.gotoEnemy(commanderAIT);
                    } catch (KeySizeException e) {
                        e.printStackTrace();
                    }
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
            /*Point2D thisPosition = agent.getPosition().pos();
            Point2D destination = new Point2D(thisPosition.getX() + speedVec, thisPosition.getY());
            agent.world.moveAgent(agent, destination);*/
            if(commanderPosX != null && commanderPosY != null){
                double[] key = {commanderPosX, commanderPosY};
                try {
                    AgentInTree commanderAIT = agent.world.getAgentsTree().search(key);
                    if(commanderAIT != null)
                        agent.gotoEnemy(commanderAIT);
                } catch (KeySizeException e) {
                    e.printStackTrace();
                }
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
