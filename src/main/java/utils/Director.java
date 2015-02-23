package main.java.utils;

import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;

public class Director {
    private AgentBuilder agentBuilder;

    public void setAgentBuilder(AgentBuilder ab) {
        agentBuilder = ab;
    }

    public void setPlatform(PlatformController platform) {
        agentBuilder.setPlatform(platform);
    }

    public AgentController getAgent() throws ControllerException {
        return agentBuilder.getAgent();
    }
}
