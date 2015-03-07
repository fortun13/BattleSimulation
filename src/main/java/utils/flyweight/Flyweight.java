package utils.flyweight;

import utils.Prototype;

import java.util.List;

/**
 * Created by Marek on 21.02.2015.
 * Interface for elements of FlyweightFactory
 */
interface Flyweight<Key, Val extends Prototype> {
    Val get(int index, List<Key> p) throws CloneNotSupportedException;
}

