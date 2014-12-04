package main.java.agents;

import edu.wlu.cs.levy.CG.KeySizeException;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import javafx.geometry.Point2D;

import java.util.ArrayList;

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

    public ArrayList<AID> getMinionsWithinRange(Point2D commanderPlace, int attractionForce, World.AgentsSides side) {
        double[] key = {commanderPlace.getX(),commanderPlace.getY()};
        ArrayList<World.AgentInTree> list = new ArrayList<>();
        try {
            world.getAgents2().nearestEuclidean(key,attractionForce).stream().filter(a -> a.side==side).forEach(list::add);
        } catch (KeySizeException e) {
            e.printStackTrace();
        }
        ArrayList<AID> ans = new ArrayList<>();
        for (World.AgentInTree a : list) {
            ans.add(new AID(a.getAgentName(),false));
        }
        return ans;
    }

    public abstract void reactToAttack(ACLMessage msg);

    public abstract boolean isMotivated();

    protected abstract void killYourself(ACLMessage msgToSend);

    public void kill() {
        world.killAgent(this);
    }
}
