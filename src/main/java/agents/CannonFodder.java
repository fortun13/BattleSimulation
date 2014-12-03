package main.java.agents;

import jade.core.AID;
import javafx.geometry.Point2D;
import main.java.utils.KdTree;

import java.util.List;

/**
 * Created by Jakub Fortunka on 18.11.14.
 *
 */
public abstract class CannonFodder extends AgentWithPosition {

    protected int condition, strength, speed, accuracy;

    //private MessageTemplate mt;

    public void setup() {

        //TODO is it going to work?

        // 0 - behaviour
        // 1 - condition
        // 2 - strength
        // 3 - speed
        // 4 - accuracy
        // 5 - agentSide
        // 6 - world
        // 7 - position

        Object[] parameters = getArguments();

        addBehaviour((ReactiveBehaviour) parameters[0]);
        this.condition = (int) parameters[1];
        this.strength = (int) parameters[2];
        this.speed = (int) parameters[3];
        this.accuracy  = (int) parameters[4];
        this.side = (World.AgentsSides) parameters[5];
        this.world = (World) parameters[6];
        this.position = (World.AgentInTree) parameters[7];
    }

    @Override
    public void doDelete() {
        world.removeAgent(this);
        super.doDelete();
    }

    @Override
    protected void takeDown() {
        world.removeAgent(this);
        super.takeDown();
    }


//    protected void takeDown() {
//        world.removeAgent(this);
//    }

    @Override
    protected World.AgentInTree getNearestEnemy() {
        return world.getNearestEnemy(this);
    }

    @Override
    protected void gotoEnemy(World.AgentInTree enemy) {
        // I don't really know...
        // I mean - here should be computed some kind of "vector" in which we will be travelling
        // If agent is in range of other agent - then set destination to closest free point
        // If not - then approach other agent as fast as possible (using computed vector)

        // Vector between agent and spotted enemy

        Point2D thisPosition = position.pos();
        Point2D enemyPosition = enemy.pos();
        int vec[] = new int[] {(int)enemyPosition.getX() - (int)thisPosition.getX(),
                                (int)enemyPosition.getY() - (int)thisPosition.getY()};
        /* Compute movement vector, for example: [4, -7] => [4/|4|, -7/|-7|] => [1, -1] */
        if (vec[0] != 0) vec[0] = vec[0]/Math.abs(vec[0]);
        if (vec[1] != 0) vec[1] = vec[1]/Math.abs(vec[1]);
        Point2D destination = new Point2D(thisPosition.getX() + vec[0], thisPosition.getY() + vec[1]);
        // Check if agent can move diagonally
        if(!world.moveAgent(this,destination)) {
            // Check if agent can move horizontally
            destination = new Point2D(thisPosition.getX() + vec[0], thisPosition.getX());
            if (!world.moveAgent(this,destination)) {
                // Check if agent can move vertically
                destination = new Point2D(thisPosition.getX(), thisPosition.getX() + vec[1]);
                world.moveAgent(this,destination);
                /*if(!world.moveAgent(this,destination)) {
                    //TODO should we program this situation?
                }*/
            }
        }
    }

    /*protected void keepPosition() {
        List<KdTree.Placed> friendlyNeighbors;
        friendlyNeighbors = world.getNeighborFriends(this, this.side);
        Point2D thisPosition = position.pos();
        double minDistance = 0, pomDistance, posX, posY;
        int index = 0;
        for(int i = 0; i < friendlyNeighbors.size(); ++i) {
            posX = friendlyNeighbors.get(i).pos().getX();
            posY = friendlyNeighbors.get(i).pos().getY();
            pomDistance = Math.sqrt(Math.pow(posX - thisPosition.getX(), 2) + Math.pow(posY - thisPosition.getY(), 2));
            if((pomDistance < minDistance) || (minDistance == 0)) {
                minDistance = pomDistance;
                index = i;
            }
            posX = friendlyNeighbors.get(index).pos().getX();
            posY = friendlyNeighbors.get(index).pos().getY();
            int vec[] = new int[] {(int)posX - (int)thisPosition.getX(),
                    (int)posY - (int)thisPosition.getY()};
            vec[0] = vec[0]/Math.abs(vec[0]);
            vec[1] = vec[1]/Math.abs(vec[1]);
            Point2D destination = new Point2D(thisPosition.getX() + vec[0], thisPosition.getY() + vec[1]);
            world.moveAgent(this,destination);
        }
     }*/

    protected void keepPosition() {
        List<KdTree.Placed> friendlyNeighbors;
        friendlyNeighbors = world.getNeighborFriends(this, this.side);
        Point2D thisPosition = position.pos();
        double vec[] = {0, 0};
        double posX, posY, srDistance = 0, pomDistance;
        for(int i = 0; i < friendlyNeighbors.size(); ++i) {
            posX = friendlyNeighbors.get(i).pos().getX();
            posY = friendlyNeighbors.get(i).pos().getY();
            srDistance = srDistance + Math.sqrt(Math.pow(posX - thisPosition.getX(), 2) + Math.pow(posY - thisPosition.getY(), 2));
        }

        for(int i = 0; i < friendlyNeighbors.size(); ++i) {
            posX = friendlyNeighbors.get(i).pos().getX();
            posY = friendlyNeighbors.get(i).pos().getY();
            pomDistance = Math.sqrt(Math.pow(posX - thisPosition.getX(), 2) + Math.pow(posY - thisPosition.getY(), 2));
            vec[0] = vec[0] + ((posX - thisPosition.getX()) * (pomDistance - srDistance))/pomDistance;
            vec[1] = vec[1] + ((posY - thisPosition.getY()) * (pomDistance - srDistance))/pomDistance;
        }
        vec[0] = Math.round(vec[0]);
        vec[1] = Math.round(vec[1]);
        if (vec[0] != 0) vec[0] = vec[0]/Math.abs(vec[0]);
        if (vec[1] != 0) vec[1] = vec[1]/Math.abs(vec[1]);
        Point2D destination = new Point2D(thisPosition.getX() + vec[0], thisPosition.getY() + vec[1]);
        world.moveAgent(this,destination);
    }

    protected abstract void attack(AID enemy);

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public int getStrength() {
        return strength;
    }

    public int getSpeed() {
        return speed;
    }

    public int getAccuracy() {
        return accuracy;
    }


}
