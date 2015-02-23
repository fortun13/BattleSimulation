package main.java.utils.flyweight;

import main.java.agents.AgentWithPosition;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Marek on 11.02.2015.
 *
 * As name points
 */
public class FlyweightFactory {
    private Flyweight<String, IconConstructor> icons = new FlyweightMap<>(new IconConstructor());

    private FlyweightFactory() {}

    private static FlyweightFactory singleton = new FlyweightFactory();
    public static FlyweightFactory getFactory() {
        return singleton;
    }

    private Flyweight<Integer, AgentWithPosition.Statistics> map = new FlyweightMap<>(new AgentWithPosition.Statistics());

    public AgentWithPosition.Statistics getStatistics(int strength, int accuracy, int speed, int attackRange) {
        List<Integer> b = new ArrayList<>(Arrays.asList(strength, accuracy, speed, attackRange));

        try {
            return map.get(0, b);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Statistics has to override clone() without throwing that exception", e);
        }
    }

    public BufferedImage getIcon(String path) {
        List<String> p = new ArrayList<>(Arrays.asList(path));

        try {
            return icons.get(0, p).get();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("IconConstructor has to override clone() without throwing that exception", e);
        }
    }
}
