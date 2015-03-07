package utils;

import jade.core.AID;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;
import agents.ReactiveBehaviour;
import agents.World;
import utils.flyweight.FlyweightFactory;

/**
 * Created by Jakub Fortunka on 21.11.14.
 *
 */

/**
 * Abstract builder for agents
 */
public abstract class AgentBuilder {
    public static final int STATS      = 0;
    public static final int POSITION   = 1;
    public static final int SERVER     = 2;
    public static final int WORLD      = 3;
    public static final int BEHAVIOUR  = 4;
    public static final int ATTRACTION = 5;
    protected PlatformController platform;
    protected Object[] parameters = new Object[9];

    Class<? extends ReactiveBehaviour> behaviourClass;
    protected String name;

    public void setPlatform(PlatformController platform) {
        this.platform = platform;
    }

    public abstract AgentController getAgent() throws ControllerException;

    public  void buildStatistics(int strength, int accuracy, int speed, int attackRange) {
        parameters[STATS] = FlyweightFactory.getFactory().getStatistics(strength, accuracy, speed, attackRange);
    }
    public void buildState(AgentInTree position, int condition) {
        parameters[POSITION]  = position;
        position.condition = condition;
    }
    public void buildName(String name) {
        this.name = name;
    }
    public void buildServer(AID server) {
        parameters[SERVER] = server;
    }
    public void buildWorld(World w) {
        parameters[WORLD] = w;
    }

    public void setBehaviourClass(Class<? extends ReactiveBehaviour> behaviourClass) {
        this.behaviourClass = behaviourClass;
    }

    public void buildBehaviour() {
        try {
            parameters[BEHAVIOUR] = behaviourClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Class<? extends ReactiveBehaviour> getBehaviour() {
        return behaviourClass;
    }
}

