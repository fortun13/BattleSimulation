package main.java.agents;

import jade.core.Agent;
import javafx.geometry.Point2D;
import main.java.utils.KdTree;

/**
 * Created by Jakub Fortunka on 20.11.14.
 */
public abstract class AgentWithPosition extends Agent implements KdTree.Placed {
    protected Point2D position;

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public abstract boolean enemyInRangeOfAttack(AgentWithPosition enemy);

}
