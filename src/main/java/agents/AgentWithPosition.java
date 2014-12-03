package main.java.agents;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;

/**
 * Created by Jakub Fortunka on 20.11.14.
 */
public abstract class AgentWithPosition extends Agent {
    //protected Point2D position;

    protected int fieldOfView = 20;

    protected int morale = 50;

    protected float previousRatio=1;

    protected float psychologicalResistance = 1;

    protected World world;

    protected World.AgentInTree position;

    protected World.AgentsSides side;

    public int getFieldOfView() { return fieldOfView; }

    public void setFieldOfView(int fov) { fieldOfView = fov; }

    protected abstract boolean enemyInRangeOfAttack(World.AgentInTree enemy);

    protected abstract World.AgentInTree getNearestEnemy();

    protected abstract void gotoEnemy(World.AgentInTree enemy);

    protected abstract void keepPosition();

    public World.AgentsSides getAgentSide() {
        return side;
    }

    public World.AgentInTree getPosition() {
        return position;
    }


    public abstract void reactToAttack(ACLMessage msg);

    public abstract boolean isMotivated();

    protected abstract void killYourself(ACLMessage msgToSend);

    public void kill() {
        world.killAgent(this);
    }
}
