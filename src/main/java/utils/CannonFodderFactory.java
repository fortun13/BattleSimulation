package main.java.utils;

import main.java.agents.Archer;
import main.java.agents.Warrior;

/**
 * Created by Jakub Fortunka on 19.11.14.
 */
abstract class CannonFodderFactory {

    public enum SimpleAgentType { Warrior, Archer }

    public static CannonFodderFactory getFactory(SimpleAgentType agentType) {
        switch(agentType) {
            case Warrior:
                return new WarriorFactory();
            case Archer:
                return new ArcherFactory();
            default:
                return null;
        }
    }
}

class WarriorFactory extends CannonFodderFactory {

    private final int condition = 40;
    private final int strength = 10;
    private final int speed = 3;
    private final int accuracy = 90;

    public Warrior createWarrior() {
        return createWarrior(condition,strength,speed,accuracy);
    }

    public Warrior createWarrior(int condition) {
        return createWarrior(condition,strength,speed,accuracy);
    }

    public Warrior createWarrior(int condition, int strength) {
        return createWarrior(condition,strength,speed,accuracy);
    }

    public Warrior createWarrior(int condition, int strength, int speed) {
        return createWarrior(condition, strength, speed, accuracy);
    }

    public Warrior createWarrior(int condition, int strength, int speed, int accuracy) {
        Warrior w = new Warrior();
        w.setCondition(condition);
        w.setStrength(strength);
        w.setSpeed(speed);
        w.setAccuracy(accuracy);
        return w;
    }
}

class ArcherFactory extends CannonFodderFactory {
    private final int condition = 30;
    private final int strength = 7;
    private final int speed = 4;
    private final int accuracy = 95;
    private final int attackRange = 4;

    public Archer createArcher() {
        return createArcher(condition, strength, speed, accuracy, attackRange);
    }

    public Archer createArcher(int condition) {
        return createArcher(condition, strength, speed, accuracy, attackRange);
    }

    public Archer createArcher(int condition, int strength) {
        return createArcher(condition, strength, speed, accuracy, attackRange);
    }

    public Archer createArcher(int condition, int strength, int speed) {
        return createArcher(condition, strength, speed, accuracy, attackRange);
    }

    public Archer createArcher(int condition, int strength, int speed, int accuracy) {
        return createArcher(condition, strength, speed, accuracy,attackRange);
    }

    public Archer createArcher(int condition, int strength, int speed, int accuracy, int attackRange) {
        Archer a = new Archer();
        a.setCondition(condition);
        a.setStrength(strength);
        a.setSpeed(speed);
        a.setAccuracy(accuracy);
        return a;
    }
}
