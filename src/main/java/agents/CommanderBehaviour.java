package main.java.agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import javafx.geometry.Point2D;
import main.java.utils.AgentInTree;

import java.util.ArrayList;

/**
 * Created by fortun on 03.12.14.
 *
 */
public class CommanderBehaviour extends ReactiveBehaviour {

    private ArrayList<AID> minions = new ArrayList<>();

    public void decideOnNextStep() {
        CannonFodder agent = (CannonFodder) myAgent;
        switch (state) {
            case 0:
                System.out.println("Follow me!");
                //TODO - get some limit for controlled minions
                minions = ((AgentWithPosition) myAgent).getMinionsWithinRange(((AgentWithPosition) myAgent).getPosition(), ((Commander)myAgent).position.p, ((Commander) myAgent).attractionForce, ((Commander)myAgent).position.side);
                /*
                getMinionsWithinRange - zwraca tylko agenta ktory poszukuje minionów (czyli commandera)
                zabezpieczyć przed zwracaniem samego siebie (o nie to chodzi)
                Dlaczego nie zwraca sąsiadów?
                1. Źle obliczony zasięg wyszukiwannia
                2. Złe działanie metody (istnieją podobne metody i działają, a więc jest to mało prawdopodobne)
                ----
                Teraz wygląda na to, że getMinionsWithinRange działa (dość) dobrze
                Tylko nie wiedzieć czemu commander wysyła wiadomość do samego siebie
                Tyle razy ilu adresatów ma w liście
                */
                if(minions == null)
                    System.out.println("Taki chuj");
                else
                    System.out.println("Nie taki chuj");
                ACLMessage commanderAddMessage = new ACLMessage(ACLMessage.REQUEST);
                commanderAddMessage.setConversationId("commander-init");
                commanderAddMessage.setContent(myAgent.getName());
                for(int i = 0; i < minions.size(); i++) {
                    System.out.println("DUPA: " + i + " " + minions.get(i).getLocalName() + " " + minions.get(i));
                    commanderAddMessage.addReceiver(minions.get(i));
                }
                agent.send(commanderAddMessage);
                state++;
                break;
            case 1:
                //TODO for now - basic, find nearest enemy and send minions to kill
                enemyPosition = ((CannonFodder)myAgent).getNearestEnemy();
                ACLMessage fightingStance = new ACLMessage(ACLMessage.REQUEST);
                minions.forEach(fightingStance::addReceiver);
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
                    double speedVec = agent.world.computeBoardCenter(agent.position.pos());
                    fightingStance.addUserDefinedParameter("speedVecXVal", String.valueOf(speedVec));
                    /*Point2D thisPosition = agent.getPosition().pos();
                    Point2D destination = new Point2D(thisPosition.getX() + speedVec, thisPosition.getY());
                    agent.world.moveAgent(agent, destination);*/
                    agent.keepPosition();
                }
                minions.forEach(fightingStance::addReceiver);
                agent.send(fightingStance);
                break;
        }
    }

    @Override
    public void handleMessage(ACLMessage msg) {
        //TODO - question is, what to do?
    }
}
