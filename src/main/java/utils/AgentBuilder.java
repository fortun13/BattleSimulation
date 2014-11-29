package main.java.utils;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;
import main.java.agents.BerserkBehaviour;
import main.java.agents.ServerAgent;
import main.java.agents.Warrior;
import main.java.agents.World;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Jakub Fortunka on 21.11.14.
 */
public abstract class AgentBuilder {
    protected AgentController agent;
    protected PlatformController platform;
    protected ArrayList<Object> parameters = new ArrayList<>(7);

    World.AgentsSides side;
    String behaviourClassname;
    World.AgentInTree position;
    World world;
    String agentName;

    public void setPlatform(PlatformController platform) {
        this.platform = platform;
    }

    public abstract AgentController getAgent() throws ControllerException;

    public abstract void buildCondition();
    public abstract void buildStrength();
    public abstract void buildSpeed();
    public abstract void buildAccuracy();
    public abstract void buildSide();
    public abstract void buildPosition();
    public abstract void buildWorld();
    public abstract void buildBehaviour();

    public abstract void setSide(World.AgentsSides side);
    public abstract void setAgentName(String agentName);
    public abstract void setPosition(World.AgentInTree position);

    public void constructAgent() {
        buildBehaviour();
        buildCondition();
        buildStrength();
        buildSpeed();
        buildAccuracy();
        buildSide();
        buildWorld();
        buildPosition();
    };
}

