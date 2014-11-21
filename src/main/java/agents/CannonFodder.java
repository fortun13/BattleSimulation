package main.java.agents;

import jade.core.AID;
import javafx.geometry.Point2D;

/**
 * Created by Jakub Fortunka on 18.11.14.
 *
 */
public abstract class CannonFodder extends AgentWithPosition {

    private int condition, strength, speed, accuracy;

    protected World world;

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


    protected void takeDown() {
        //world.killAgent(this);
    }

    @Override
    protected World.AgentInTree getNearestEnemy() {
        //TODO
        //have to have representation of environment to do something with it
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
        int vec[] = new int[] {(int)thisPosition.getX() - (int)enemyPosition.getX(),
                                (int)thisPosition.getY() - (int)enemyPosition.getY()};
        /* Compute movement vector, for example: [4, -7] => [4/|4|, -7/|-7|] => [1, -1] */
        vec[0] = vec[0]/Math.abs(vec[0]);
        vec[1] = vec[1]/Math.abs(vec[1]);
        Point2D destination = new Point2D(thisPosition.getX() + vec[0], thisPosition.getX() + vec[1]);
        // Check if agent can move in skew vector (Po ukośnym wektorze :D)
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

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }


}
