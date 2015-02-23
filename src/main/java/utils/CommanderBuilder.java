package main.java.utils;

import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import main.java.agents.ReactiveBehaviour;

/**
 * Created by KrzysiekH on 2014-12-13.
 *
 */
public class CommanderBuilder extends AgentBuilder {

    public CommanderBuilder(Class<? extends ReactiveBehaviour> b) {
        behaviourClass = b;
    }

    @Override
    public AgentController getAgent() throws ControllerException {
        buildBehaviour();
        return platform.createNewAgent(name, "main.java.agents.Commander", parameters.clone());
    }

    public void buildAttractionForce(int attractionForce) {
        parameters[ATTRACTION] = attractionForce;
    }

}
