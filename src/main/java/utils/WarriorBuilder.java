package utils;

import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import agents.ReactiveBehaviour;

public class WarriorBuilder extends AgentBuilder {

    public WarriorBuilder(Class<? extends ReactiveBehaviour> b) {
        behaviourClass = b;
    }

    @Override
    public AgentController getAgent() throws ControllerException {
        buildBehaviour();
        return platform.createNewAgent(name, "agents.Warrior", parameters.clone());
    }
}
