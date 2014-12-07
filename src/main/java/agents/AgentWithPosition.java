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

    public boolean isMotivated() {
        int [] count;
        count = world.countFriendFoe(this);
        //System.out.println("Friends: " + count[0] + " Enemies: " + count[1]);
        if (count[1] == 0) {
            morale += 2;
            return true;
        }
        double ratio = ((double)count[0])/((double)count[1]);
        //System.out.println("Ratio: " + ratio);
        if (ratio < psychologicalResistance && ratio < previousRatio)
            morale -= (1/ratio +2);
        if (ratio >= 1 && morale<50)
            morale += ratio;
        previousRatio = ratio;
        //System.out.println(getLocalName() + " Morale: " + morale);
        return morale > 0;
    }

    protected abstract void killYourself(ACLMessage msgToSend);

}
