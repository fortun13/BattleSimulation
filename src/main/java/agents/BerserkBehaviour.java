package main.java.agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 * Created by Jakub Fortunka on 18.11.14.
 * Behaviour presented in a frenzy of battle
 */
public class BerserkBehaviour extends ReactiveBehaviour {

    @Override
    public void decideOnNextStep() {
        if (((CannonFodder)myAgent).condition <= 0) {
            ACLMessage m = new ACLMessage(ACLMessage.INFORM);
            //TODO it's not good practice - i just want to test if this will work
            m.addReceiver(new AID("server",false));
            m.setConversationId("ended-computation");
            myAgent.send(m);
            return ;
        }
        switch(state) {
            case 0:
                //findEnemy, if enemyFound goto state 2
                enemyPosition = ((AgentWithPosition)myAgent).getNearestEnemy();
                if (enemyPosition != null) {
                    System.out.println("Found enemy!" + myAgent.getName());
                    enemy = new AID(enemyPosition.getAgentName(),true);
                    ((AgentWithPosition)myAgent).gotoEnemy(enemyPosition);
                    state++;
                }
                else {
                    //moveSomewhere
                    System.out.println("Staying in place (for now...)");
                }
                break;
            case 1:
                // I assume that "world" will kill agent, so, when enemy will die, Agent enemy will become null (not sure if it's good thinking though)

                if (enemy == null) {
                    enemyPosition = ((AgentWithPosition)myAgent).getNearestEnemy();
                    if (enemyPosition == null) {
                        state--;
                        break;
                    } else {
                        enemy = new AID(enemyPosition.getAgentName(),true);
                    }
                }
                if (((AgentWithPosition)myAgent).enemyInRangeOfAttack(enemyPosition))
                    ((CannonFodder)myAgent).attack(enemy);
                else
                    ((CannonFodder)myAgent).gotoEnemy(enemyPosition);
//                agent.attack(enemy);
                break;
        }
    }

}
