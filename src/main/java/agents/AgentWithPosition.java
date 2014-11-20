package main.java.agents;

import jade.core.Agent;
import javafx.geometry.Point2D;

/**
 * Created by Jakub Fortunka on 20.11.14.
 */
public abstract class AgentWithPosition extends Agent {
    protected Point2D position;

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public abstract boolean enemyInRangeOfAttack(AgentWithPosition enemy);

}
