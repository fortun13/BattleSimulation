package main.java.utils;

import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import main.java.agents.ReactiveBehaviour;

/**
 * Created by KrzysiekH on 2014-12-13.
 *
 */
public class CommanderBuilder extends AgentBuilder {

    public CommanderBuilder(Settings s, Class<? extends ReactiveBehaviour> b) {
        super(s);
        behaviourClass = b;
    }

    @Override
    public AgentController getAgent() throws ControllerException {
        return platform.createNewAgent(settings.name,"main.java.agents.Commander",parameters.clone());
    }

    @Override
    public void buildCondition() {
        parameters[1] = 60;
    }

    @Override
    public void buildStrength() {
        parameters[2] = 10;
    }

    @Override
    public void buildSpeed() {
        parameters[3] = 7;
    }

    @Override
    public void buildAccuracy() {
        parameters[4] = 95;
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

    public void buildAttractionForce() {
        parameters[8] = 100;
    }

    @Override
    protected void buildAttackRange() {
        parameters[7] = 3;
    }

    @Override
    public void constructAgent() {
        super.constructAgent();
        buildAttractionForce();
    }
}
