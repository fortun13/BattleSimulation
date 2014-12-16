package main.java.agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import javafx.geometry.Point2D;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by fortun on 03.12.14.
 *
 */
public class CommanderBehaviour extends ReactiveBehaviour {
    private int innerState;

    private static final int FOLLOWIN = 1;
    private static final int SEARCH = 0;

    private ArrayList<AID> minions = new ArrayList<>();

    public void decideOnNextStep() {
        CannonFodder agent = (CannonFodder) myAgent;
        double posX = ((CannonFodder) myAgent).getPosition().pos().getX();
        double posY = ((CannonFodder) myAgent).getPosition().pos().getY();
        if (enemyPosition == null || enemyPosition.isDead) {
            innerState = SEARCH;
        }
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
                switch(innerState) {
                    case SEARCH:
                        enemyPosition = agent.getNearestEnemy();
                        if (enemyPosition != null) {
                            fightingStance.setConversationId("stance-fight");
                            enemy = new AID(enemyPosition.getAgentName(),true);
                            agent.gotoEnemy(enemyPosition);
                            innerState = FOLLOWIN;
                        }
                        else {
                            fightingStance.setConversationId("stance-march");
                            ((Commander) myAgent).goToPoint(((Commander) myAgent).world.returnBoardCenter());
                        }
                        break;
                    case FOLLOWIN:
                        fightingStance.setConversationId("stance-fight");
                        if (agent.enemyInRangeOfAttack(enemyPosition)) {
                            agent.setSpeedVector(0, 0);
                            agent.attack(enemy, enemyPosition);
                        } else {
                            agent.gotoEnemy(enemyPosition);
                            break;
                        }
                        break;
                }
                agent.send(fightingStance);
                break;

        }
    }

    @Override
    public void handleMessage(ACLMessage msg) {
    }
}
