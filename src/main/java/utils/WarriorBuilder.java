package main.java.utils;

import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import main.java.agents.ReactiveBehaviour;

public class WarriorBuilder extends AgentBuilder {

    public WarriorBuilder(Class<? extends ReactiveBehaviour> b) {
        behaviourClass = b;
    }

    @Override
    public AgentController getAgent() throws ControllerException {
        buildBehaviour();
        return platform.createNewAgent(name, "main.java.agents.Warrior", parameters.clone());
    }
}
