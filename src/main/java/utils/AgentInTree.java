package utils;

import agents.AgentType;
import javafx.geometry.Point2D;
import agents.ReactiveBehaviour;
import agents.World;

/**
 * Class which represents state of our agent - it's
 */
public class AgentInTree implements Placed {

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

    public AgentType type;

    public AgentInTree(String agentName,World.AgentsSides side, Point2D position, AgentType type, Class<? extends ReactiveBehaviour> behaviourClass) {
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

