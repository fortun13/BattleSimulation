package main.java.utils;

import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import main.java.agents.ReactiveBehaviour;

/**
 * Created by Jakub Fortunka on 21.11.14.
 *
 */
public class ArcherBuilder extends AgentBuilder {

    public ArcherBuilder(Settings s, Class<? extends ReactiveBehaviour> b) {
        super(s);
        behaviourClass = b;
    }

    @Override
    public AgentController getAgent() throws ControllerException {
        return platform.createNewAgent(settings.name,"main.java.agents.Archer",parameters.clone());
    }

    @Override
    public void buildCondition() {
        parameters[1] = settings.condition;
    }

    @Override
    public void buildStrength() {
        parameters[2] = settings.strength;
    }

    @Override
    public void buildSpeed() {
        parameters[3] = settings.speed;
    }

    @Override
    public void buildAccuracy() {
        parameters[4] = settings.accuracy;
    }

    @Override
    public void buildPosition() {
        parameters[6] = settings.position;
    }

    @Override
    public void buildWorld() {
        parameters[5] = settings.world;
    }

    @Override
    public void buildBehaviour() {
        try {
            parameters[0] = behaviourClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void buildAttackRange() {
        parameters[7] = settings.attackRange;
    }

    @Override
    public void constructAgent() {
        super.constructAgent();
    }
}
