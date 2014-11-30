package main.java.utils;

import jade.core.AID;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import main.java.agents.BerserkBehaviour;
import main.java.agents.World;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Created by Jakub Fortunka on 21.11.14.
 *
 */
public class ArcherBuilder extends AgentBuilder {

    public ArcherBuilder(AID serverAID, Class<BerserkBehaviour> b, World.AgentsSides s, World w) {
        super(serverAID);
        side = s;
        behaviourClass = b;
        world = w;
    }

    @Override
    public AgentController getAgent() throws ControllerException {
        return platform.createNewAgent(agentName,"main.java.agents.Archer",parameters.clone());
    }

    @Override
    public void buildCondition() {
        parameters[1] = 35;
    }

    @Override
    public void buildStrength() {
        parameters[2] = 4;
    }

    @Override
    public void buildSpeed() {
        parameters[3] = 5;
    }

    @Override
    public void buildAccuracy() {
        parameters[4] = 95;
    }

    @Override
    public void buildSide() {
        parameters[5] = side;
    }

    @Override
    public void buildPosition() {
        parameters[7] = position;
    }

    @Override
    public void buildWorld() {
        parameters[6] = world;
    }

    @Override
    public void buildBehaviour() {
        //parameters[0] = behaviour;
        try {
            Type[] args = {AID.class};
            for (Constructor<?> constructor : behaviourClass.getDeclaredConstructors()) {
                if (Arrays.equals(constructor.getParameterTypes(), args)) {
                    constructor.setAccessible(true);
                    parameters[0] = constructor.newInstance(serverAID);
                    return;
                }
            }
            throw new RuntimeException("no constructor found with parameters: " + Arrays.toString(args));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSide(World.AgentsSides side) {
        this.side = side;
    }

    @Override
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    @Override
    public void setPosition(World.AgentInTree position) {
        this.position = position;
    }

    public void buildAttackRange() {
        parameters[8] = 5;
    }

    @Override
    public void constructAgent() {
        super.constructAgent();
        buildAttackRange();
    }
}
