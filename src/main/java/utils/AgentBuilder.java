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
    protected PlatformController platform;
    protected Object[] parameters = new Object[9];

    Class<? extends ReactiveBehaviour> behaviourClass;
    protected Settings settings;


    protected AgentBuilder(Settings s) {
        settings = s;
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

    protected abstract void buildAttackRange();

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
        buildAttackRange();
    }


    public Class<? extends ReactiveBehaviour> getBehaviour() {
        return behaviourClass;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public static class Settings {
        public AID server;
        public World world;
        public String name;
        public AgentInTree position;
        public Integer condition;
        public Integer strength;
        public Integer speed;
        public Integer accuracy;
        public Integer attackRange;
        public Integer attractionForce;
    }
}

