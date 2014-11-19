package main.java.agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;


/**
 * Created by Jakub Fortunka on 18.11.14.
 */
public class BerserkerBehaviour extends Behaviour {

    // It's bad, I know it, but for now - have to cast so i can use methods from CannonFodder
    CannonFodder agent = (CannonFodder) this.myAgent;
    int state = 0;
    CannonFodder enemy;
    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            // probably can do it with msg.getPrformative and some ACL static fields, but for now let it be string
            switch(msg.getContent()) {
                case "new-step":
                    doAction();
                    break;
                case "battle-ended":
                    System.out.println("WE WON!!");
                    state = 2;
                    return;
            }
        } else {
            block();
        }
    }

    private void doAction() {
        switch(state) {
            case 0:
                //findEnemy, if enemyFound goto state 2
                enemy = agent.getNearestEnemy();
                if (enemy != null) {
                    agent.gotoEnemy(enemy);
                    state++;
                }
                else {
                    //moveSomewhere
                    agent.moveSomewhere();
                }
                break;
            case 1:
                // I assume that "world" will kill agent, so, when enemy will die, Agent enemy will become null (not sure if it's good thinking though)
                if (enemy == null) {
                    enemy = agent.getNearestEnemy();
                    if (enemy == null) {
                        state--;
                        break;
                    }
                }
//                agent.attack(enemy);
                break;
        }
    }

    @Override
    public boolean done() {
        return state == 2;
    }
}
