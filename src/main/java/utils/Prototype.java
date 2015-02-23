package main.java.utils;

import java.util.List;

/**
 * Created by Marek on 21.02.2015.
 * Interface of hate
 */
public interface Prototype<Key, Val> extends Cloneable {
    Val clone() throws CloneNotSupportedException;
    void setup(List<Key> keys);
}
