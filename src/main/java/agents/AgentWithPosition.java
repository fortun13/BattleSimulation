package main.java.agents;

import jade.core.Agent;
import javafx.geometry.Point2D;
import main.java.utils.KdTree;

/**
 * Created by Jakub Fortunka on 20.11.14.
 */
public abstract class AgentWithPosition extends Agent implements KdTree.Placed {
    protected Point2D position;

    protected int fieldOfView = 20;

    public int getFieldOfView() { return fieldOfView; }

    public void setFieldOfView(int fov) { fieldOfView = fov; }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public abstract boolean enemyInRangeOfAttack(AgentWithPosition enemy);

    public abstract World.AgentsSides getAgentSide();
}
