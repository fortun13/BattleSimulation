package utils;

import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import agents.ReactiveBehaviour;

/**
 * Created by Jakub Fortunka on 21.11.14.
 *
 */
public class ArcherBuilder extends AgentBuilder {

    public ArcherBuilder(Class<? extends ReactiveBehaviour> b) {
        behaviourClass = b;
    }

    @Override
    public AgentController getAgent() throws ControllerException {
        buildBehaviour();
        return platform.createNewAgent(name, "agents.Archer", parameters.clone());
    }
}
