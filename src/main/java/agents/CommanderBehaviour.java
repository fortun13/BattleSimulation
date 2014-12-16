package main.java.agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import javafx.geometry.Point2D;
import java.util.ArrayList;

/**
 * Created by fortun on 03.12.14.
 *
 */
public class CommanderBehaviour extends ReactiveBehaviour {

    private ArrayList<AID> minions = new ArrayList<>();

    public void decideOnNextStep() {
        CannonFodder agent = (CannonFodder) myAgent;
        double posX = ((CannonFodder) myAgent).getPosition().pos().getX();
        double posY = ((CannonFodder) myAgent).getPosition().pos().getY();
        //System.out.println("Gromadzę miniony " + state + " " + myAgent.getLocalName());
        switch (state) {
            case 0:
                //TODO - get some limit for controlled minions
                minions = ((AgentWithPosition) myAgent).getMinionsWithinRange((Commander)myAgent);
                ACLMessage commanderAddMessage = new ACLMessage(ACLMessage.REQUEST);
                commanderAddMessage.setConversationId("commander-init");
                commanderAddMessage.setContent(myAgent.getName());
                commanderAddMessage.addUserDefinedParameter("commanderPosX", String.valueOf(posX));
                commanderAddMessage.addUserDefinedParameter("commanderPosY", String.valueOf(posY));
                minions.forEach(commanderAddMessage::addReceiver);
                agent.send(commanderAddMessage);
                state++;
                break;
            case 1:
                ACLMessage fightingStance = new ACLMessage(ACLMessage.REQUEST);
                fightingStance.setContent(myAgent.getName());
                fightingStance.addUserDefinedParameter("commanderPosX", String.valueOf(posX));
                fightingStance.addUserDefinedParameter("commanderPosY", String.valueOf(posY));
                minions.forEach(fightingStance::addReceiver);

                ArrayList<AID> enemiesInRange = ((CannonFodder) myAgent).enemyInRange(agent);
                if (enemiesInRange.size() != 0) {
                    fightingStance.setConversationId("stance-fight");
                    enemyPosition = ((CannonFodder)myAgent).getNearestEnemy();
                    AID enemy = new AID(enemyPosition.getAgentName(), true);
                    agent.gotoEnemy(enemyPosition);
                    if (agent.enemyInRangeOfAttack(enemyPosition)) {
                        agent.setSpeedVector(0, 0);
                        agent.attack(enemy, enemyPosition);
                    }
                    //System.out.println("Uderzam! " + myAgent.getLocalName());
                }
                else {
                    fightingStance.setConversationId("stance-march");
                    double speedVec = agent.world.computeBoardCenter(agent.position.pos());
                    fightingStance.addUserDefinedParameter("speedVecXVal", String.valueOf(speedVec));
                    Point2D thisPosition = agent.getPosition().pos();
                    Point2D destination = new Point2D(thisPosition.getX() + speedVec, thisPosition.getY());
                    agent.world.moveAgent(agent, destination);
                }
                agent.send(fightingStance);
                break;
        }
    }

    @Override
    public void handleMessage(ACLMessage msg) {
    }
}
