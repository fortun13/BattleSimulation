package main.java.utils;

import javafx.geometry.Point2D;
import main.java.agents.ReactiveBehaviour;
import main.java.agents.World;

/**
 * Created by Fortun on 2014-12-07.
 *
 */

/**
 * Class which represents state of our agent - it's
 */
public class AgentInTree implements KdTree.Placed {

    public Point2D p;
    public double[] speed = new double[2];
    public World.AgentsSides side;
    private String agentName;
    public boolean isDead = false;

    public Class<? extends ReactiveBehaviour> behaviourClass;

    public int condition;

    public double morale = 50;

    public double getAngle() {
        return speed[0];
    }

    public double getSpeed() {
        return speed[1];
    }

    public World.AgentType type;

    public AgentInTree(String agentName,World.AgentsSides side, Point2D position, World.AgentType type, Class<? extends ReactiveBehaviour> behaviourClass) {
        this.agentName = agentName;
        this.side = side;
        p = position;
        this.type = type;
        this.behaviourClass = behaviourClass;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String name) {
        agentName = name;
    }

    @Override
    public Point2D pos() {
        return p;
    }

    public void setPosition(Point2D p) {
        this.p = p;
    }
}

