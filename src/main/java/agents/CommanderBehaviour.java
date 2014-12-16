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
import java.util.Collections;

/**
 * Created by fortun on 03.12.14.
 *
 */
public class CommanderBehaviour extends ReactiveBehaviour {
    private static final int FOLLOWIN = 1;
    private static final int SEARCH = 0;
    private static final int LIMIT = 10;

    private ArrayList<AID> minions = new ArrayList<AID>();

    public void decideOnNextStep() {
        CannonFodder agent = (CannonFodder) myAgent;
        double posX = ((CannonFodder) myAgent).getPosition().pos().getX();
        double posY = ((CannonFodder) myAgent).getPosition().pos().getY();
        if (minions.size() < LIMIT) {
            //TODO - get some limit for controlled minions
            ArrayList<AID> minions_pom = ((AgentWithPosition) myAgent).getMinionsWithinRange((Commander) myAgent);
            int i = 0;
            while (i < minions_pom.size() && minions.size() < LIMIT) {
                if (!minions.contains(minions_pom.get(i)))
                    minions.add(minions_pom.get(i));
                ++i;
            }

            ACLMessage commanderAddMessage = new ACLMessage(ACLMessage.REQUEST);
            commanderAddMessage.setConversationId("commander-init");
            commanderAddMessage.setContent(myAgent.getName());
            commanderAddMessage.addUserDefinedParameter("commanderPosX", String.valueOf(posX));
            commanderAddMessage.addUserDefinedParameter("commanderPosY", String.valueOf(posY));
            minions.forEach(commanderAddMessage::addReceiver);
            agent.send(commanderAddMessage);
        }

        ACLMessage fightingStance = new ACLMessage(ACLMessage.REQUEST);
        fightingStance.setContent(myAgent.getName());
        fightingStance.addUserDefinedParameter("commanderPosX", String.valueOf(posX));
        fightingStance.addUserDefinedParameter("commanderPosY", String.valueOf(posY));
        minions.forEach(fightingStance::addReceiver);

        if (enemyPosition == null || enemyPosition.isDead)
            state = SEARCH;
        switch (state) {
            case SEARCH:
                enemyPosition = agent.getNearestEnemy();
                if (enemyPosition != null) {
                    fightingStance.setConversationId("stance-fight");
                    enemy = new AID(enemyPosition.getAgentName(), true);
                    agent.gotoEnemy(enemyPosition);
                    state = FOLLOWIN;
                } else {
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
    }

    @Override
    public void handleMessage(ACLMessage msg) {
        switch(msg.getConversationId()) {
            case "minion-dead":
                minions.remove(msg.getSender());
                break;
        }
    }
}
