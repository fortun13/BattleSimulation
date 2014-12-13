package main.java.utils;

import jade.core.AID;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import main.java.agents.ReactiveBehaviour;
import main.java.agents.World;

/**
 * Created by KrzysiekH on 2014-12-13.
 */
public class CommanderBuilder extends AgentBuilder {

    public CommanderBuilder(AID serverAID, Class<? extends ReactiveBehaviour> b, World w) {
        super(serverAID);
        behaviourClass = b;
        world = w;
    }

    @Override
    public AgentController getAgent() throws ControllerException {
        return platform.createNewAgent(agentName,"main.java.agents.Commander",parameters.clone());
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
        parameters[3] = 4;
    }

    @Override
    public void buildAccuracy() {
        parameters[4] = 95;
    }

    @Override
    public void buildPosition() {
        parameters[6] = position;
    }

    @Override
    public void buildWorld() {
        parameters[5] = world;
    }

    @Override
    public void buildBehaviour() {
        try {
            parameters[0] = behaviourClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    @Override
    public void setPosition(AgentInTree position) {
        this.position = position;
    }

    @Override
    public void constructAgent() { super.constructAgent(); }
}
