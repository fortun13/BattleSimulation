package main.java.agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 * Created by Fortun on 2014-12-03.
 */
public class CommanderMinionBehaviour extends ReactiveBehaviour {

    AID commander;
    boolean stance = false;

    public CommanderMinionBehaviour(AID serverAID, AID commander) {
        super(serverAID);
        this.commander = commander;
    }

    @Override
    public void handleMessage(ACLMessage msg) {
        switch(msg.getConversationId()) {
            case "stance-fight":
                stance = true;
                //TODO - get enemyPosition somehow...
                break;
            case "stance-march":
                stance = false;
                break;
        }
    }

    @Override
    public void decideOnNextStep() {
        switch (state) {
            case 0:
                if (enemyPosition != null)
                    state++;
                break;
            case 1:
                if (enemyPosition != null) {
                    if (((CannonFodder)myAgent).enemyInRangeOfAttack(enemyPosition))
                        doAction(() -> ((CannonFodder) myAgent).attack(enemy,enemyPosition));
                    else
                        doAction(() -> ((CannonFodder)myAgent).gotoEnemy(enemyPosition));
                } else {
                    state--;
                }
                break;
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
