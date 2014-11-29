package main.java.utils;

import jade.core.behaviours.Behaviour;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import main.java.agents.BerserkBehaviour;
import main.java.agents.World;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class WarriorBuilder extends AgentBuilder {

    public WarriorBuilder(String b, World.AgentsSides s, World w) {
        side = s;
        behaviourClassname = b;
        world = w;
    }

    @Override
    public AgentController getAgent() throws ControllerException {
        return platform.createNewAgent(agentName,"main.java.agents.Warrior",parameters.toArray());
    }

    @Override
    public void buildCondition() {
        parameters.add(1,40);
    }

    @Override
    public void buildStrength() {
        parameters.add(2,5);
    }

    @Override
    public void buildSpeed() {
        parameters.add(3,3);
    }

    @Override
    public void buildAccuracy() {
        parameters.add(4,90);
    }

    @Override
    public void buildSide() {
        parameters.add(5,side);
    }

    @Override
    public void buildPosition() {
        parameters.add(7,position);
    }

    @Override
    public void buildWorld() {
        parameters.add(6,world);
    }

    @Override
    public void buildBehaviour() {
        //parameters.add(0,behaviour);
        try {
            parameters.add(0, Class.forName(behaviourClassname).getConstructor().newInstance());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        
        //parameters.add(0,new BerserkBehaviour());
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

    @Override
    public void constructAgent() {
        super.constructAgent();
    }
}
