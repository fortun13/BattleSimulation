package main.java.utils;

import jade.core.AID;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;
import main.java.agents.ReactiveBehaviour;
import main.java.agents.World;

/**
 * Created by Jakub Fortunka on 21.11.14.
 *
 */
public abstract class AgentBuilder {
    protected AID serverAID;
    protected PlatformController platform;
    protected Object[] parameters = new Object[9];

    Class<? extends ReactiveBehaviour> behaviourClass;
    AgentInTree position;
    World world;
    String agentName;

    protected AgentBuilder(AID serverAID) {
        this.serverAID = serverAID;
    }

    public void setPlatform(PlatformController platform) {
        this.platform = platform;
    }

    public abstract AgentController getAgent() throws ControllerException;

    public abstract void buildCondition();
    public abstract void buildStrength();
    public abstract void buildSpeed();
    public abstract void buildAccuracy();
    public abstract void buildPosition();
    public abstract void buildWorld();
    public abstract void buildBehaviour();

    public abstract void setAgentName(String agentName);
    public abstract void setPosition(AgentInTree position);

    public void setBehaviourClass(Class<? extends ReactiveBehaviour> behaviourClass) {
        this.behaviourClass = behaviourClass;
    }

    public void constructAgent() {
        buildBehaviour();
        buildCondition();
        buildStrength();
        buildSpeed();
        buildAccuracy();
        buildWorld();
        buildPosition();
    }

    public Class<? extends ReactiveBehaviour> getBehaviour() {
        return behaviourClass;
    }
}

