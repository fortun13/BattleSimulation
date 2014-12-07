package main.java.utils;

import javafx.geometry.Point2D;
import main.java.agents.World;

/**
 * Created by Fortun on 2014-12-07.
 */
public class AgentInTree implements KdTree.Placed {

    public Point2D p;
    public World.AgentsSides side;
    private String agentName;
    public boolean isDead = false;

    public World.AgentType type;

    public AgentInTree(String agentName,World.AgentsSides side, Point2D position, World.AgentType type) {
        this.agentName = agentName;
        this.side = side;
        p = position;
        this.type = type;
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

