package main.java.utils;

import jade.core.behaviours.Behaviour;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import main.java.agents.World;

/**
 * Created by Jakub Fortunka on 21.11.14.
 */
public class ArcherBuilder extends AgentBuilder {

    public ArcherBuilder(Behaviour b, World.AgentsSides s, World w) {
        side = s;
        behaviour = b;
        world = w;
    }

    @Override
    public AgentController getAgent() throws ControllerException {
        return platform.createNewAgent(agentName,"java.main.agents.Archer",parameters.toArray());
    }

    @Override
    public void buildCondition() {
        parameters.add(1,35);
    }

    @Override
    public void buildStrength() {
        parameters.add(2,4);
    }

    @Override
    public void buildSpeed() {
        parameters.add(3,5);
    }

    @Override
    public void buildAccuracy() {
        parameters.add(4,95);
    }

    @Override
    public void buildSide() {
        parameters.add(5,side);
    }

    @Override
    public void buildPosition() {
        parameters.add(7,position);
    }

    @Override
    public void buildWorld() {
        parameters.add(6,world);
    }

    @Override
    public void buildBehaviour() {
        parameters.add(0,behaviour);
    }

    @Override
    public void setSide(World.AgentsSides side) {
        this.side = side;
    }

    @Override
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    @Override
    public void setPosition(World.AgentInTree position) {
        this.position = position;
    }

    public void buildAttackRange() {
        parameters.add(8,5);
    }

    @Override
    public void constructAgent() {
        super.constructAgent();
        buildAttackRange();
    }
}
