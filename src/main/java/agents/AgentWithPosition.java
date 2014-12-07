package main.java.agents;

import edu.wlu.cs.levy.CG.KeySizeException;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import javafx.geometry.Point2D;
import main.java.utils.AgentInTree;

import java.util.ArrayList;

/**
 * Created by Jakub Fortunka on 20.11.14.
 */
public abstract class AgentWithPosition extends Agent {
    //protected Point2D position;

    protected int fieldOfView = 20;

    protected double morale = 50;

    protected double previousRatio=1;

    protected double psychologicalResistance = 0.7;

    protected World world;

    protected AgentInTree position;

    public int getFieldOfView() { return fieldOfView; }

    public void setFieldOfView(int fov) { fieldOfView = fov; }

    protected abstract boolean enemyInRangeOfAttack(AgentInTree enemy);

    protected abstract AgentInTree getNearestEnemy();

    protected abstract void gotoEnemy(AgentInTree enemy);

    protected abstract void keepPosition();

    public AgentInTree getPosition() {
        return position;
    }

    public ArrayList<AID> getMinionsWithinRange(Point2D commanderPlace, int attractionForce, World.AgentsSides side) {
        double[] key = {commanderPlace.getX(),commanderPlace.getY()};
        ArrayList<AgentInTree> list = new ArrayList<>();
        try {
            world.getAgentsTree().nearestEuclidean(key,attractionForce).stream().filter(a -> a.side==side).forEach(list::add);
        } catch (KeySizeException e) {
            e.printStackTrace();
        }
        ArrayList<AID> ans = new ArrayList<>();
        for (AgentInTree a : list) {
            ans.add(new AID(a.getAgentName(),false));
        }
        return ans;
    }

    public abstract void reactToAttack(ACLMessage msg);

    public abstract boolean isMotivated();

    protected abstract void killYourself(ACLMessage msgToSend);

}
