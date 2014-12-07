package main.java.agents;

import jade.core.AID;
import javafx.geometry.Point2D;
import main.java.utils.AgentInTree;

import java.util.List;

/**
 * Created by Jakub Fortunka on 18.11.14.
 *
 */
public abstract class CannonFodder extends AgentWithPosition {

    protected int condition, strength, speed, accuracy;

    public void setup() {
        // 0 - behaviour
        // 1 - condition
        // 2 - strength
        // 3 - speed
        // 4 - accuracy
        // 5 - world
        // 6 - position

        Object[] parameters = getArguments();

        addBehaviour((ReactiveBehaviour) parameters[0]);
        this.condition = (int) parameters[1];
        this.strength = (int) parameters[2];
        this.speed = (int) parameters[3];
        this.accuracy = (int) parameters[4];
        this.world = (World) parameters[5];
        this.position = (AgentInTree) parameters[6];
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

    @Override
    protected AgentInTree getNearestEnemy() {
        return world.getNearestEnemy(this);
    }

    @Override
    protected void gotoEnemy(AgentInTree enemy) {
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

    protected void keepPosition() {
        //List<KdTree.Placed> friendlyNeighbors;
        List<AgentInTree> friendlyNeighbors;

        friendlyNeighbors = world.getNeighborFriends(this, this.position.side);

        Point2D thisPosition = position.pos();
        double vec[] = {0, 0};
        double posX, posY, srDistance = 0, pomDistance;
        for (AgentInTree friendlyNeighbor : friendlyNeighbors) {
            posX = friendlyNeighbor.pos().getX();
            posY = friendlyNeighbor.pos().getY();
            srDistance = srDistance + Math.sqrt(Math.pow(posX - thisPosition.getX(), 2) + Math.pow(posY - thisPosition.getY(), 2));
        }
        for (AgentInTree friendlyNeighbor : friendlyNeighbors) {
            posX = friendlyNeighbor.pos().getX();
            posY = friendlyNeighbor.pos().getY();
            pomDistance = Math.sqrt(Math.pow(posX - thisPosition.getX(), 2) + Math.pow(posY - thisPosition.getY(), 2));
            vec[0] = vec[0] + ((posX - thisPosition.getX()) * (pomDistance - srDistance)) / pomDistance;
            vec[1] = vec[1] + ((posY - thisPosition.getY()) * (pomDistance - srDistance)) / pomDistance;
        }
        vec[0] = Math.round(vec[0]);
        vec[1] = Math.round(vec[1]);
        if (vec[0] != 0) vec[0] = vec[0]/Math.abs(vec[0]);
        if (vec[1] != 0) vec[1] = vec[1]/Math.abs(vec[1]);
        // Just move your ass...
        if(vec[0] == 0.0 && vec[1] == 0.0){
            vec[0] = world.computeBoardCenter(this.position.pos());
        }
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
