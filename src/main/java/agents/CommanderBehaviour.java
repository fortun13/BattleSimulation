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

                enemyPosition = ((Commander) myAgent).getNearestEnemy();
                if (enemyPosition != null) {
                    fightingStance.setConversationId("stance-fight");
                    AID enemy = new AID(enemyPosition.getAgentName(), true);
                    agent.gotoEnemy(enemyPosition);
                    if (agent.enemyInRangeOfAttack(enemyPosition)) {
                        agent.setSpeedVector(0, 0);
                        agent.attack(enemy, enemyPosition);
                    }
                }
                else {
                    fightingStance.setConversationId("stance-march");
                    double [] center = ((Commander) myAgent).world.returnBoardCenter();
                    Point2D destination = new Point2D(center[0], center[1]);
                    ((Commander) myAgent).goToPoint(destination);
                }
                agent.send(fightingStance);
                break;
        }
    }

    @Override
    public void handleMessage(ACLMessage msg) {
    }
}
