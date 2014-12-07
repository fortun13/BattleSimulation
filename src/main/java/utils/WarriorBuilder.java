package main.java.utils;

import jade.core.AID;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import main.java.agents.ReactiveBehaviour;
import main.java.agents.World;

public class WarriorBuilder extends AgentBuilder {

    public WarriorBuilder(AID serverAID, Class<? extends ReactiveBehaviour> b, World w) {
        super(serverAID);
        behaviourClass = b;
        world = w;
    }

    @Override
    public AgentController getAgent() throws ControllerException {
        return platform.createNewAgent(agentName, "main.java.agents.Warrior", parameters.clone());
    }

    @Override
    public void buildCondition() {
        parameters[1] = 40;
    }

    @Override
    public void buildStrength() {
        parameters[2] = 5;
    }

    @Override
    public void buildSpeed() {
        parameters[3] = 3;
    }

    @Override
    public void buildAccuracy() {
        parameters[4] = 90;
    }

    @Override
    public void buildPosition() {
        parameters[6] = position;
    }

    @Override
    public void buildWorld() {
        parameters[5] = world;
    }

    @Override
    public void buildBehaviour() {
        //parameters[0] = behaviour;
        try {
            parameters[0] = behaviourClass.newInstance();
//            Type[] args = {AID.class};
//            for (Constructor<?> constructor : behaviourClass.getDeclaredConstructors()) {
//                if (Arrays.equals(constructor.getParameterTypes(), args)) {
//                    constructor.setAccessible(true);
//                    parameters[0] = constructor.newInstance(serverAID);
//                    return;
//                }
//            }
//            throw new RuntimeException("no constructor found with parameters: " + Arrays.toString(args));
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    @Override
    public void setPosition(AgentInTree position) {
        this.position = position;
    }

    @Override
    public void constructAgent() {
        super.constructAgent();
    }
}
