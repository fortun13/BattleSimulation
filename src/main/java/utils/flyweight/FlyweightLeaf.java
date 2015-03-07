package utils.flyweight;

import utils.Prototype;

import java.util.List;

/**
 * Created by Marek on 21.02.2015.
 * Final flyweight
 */
public class FlyweightLeaf<Key, Val extends Prototype> implements Flyweight<Key,Val> {
    private Val val;

    @SuppressWarnings("unchecked")
    public FlyweightLeaf(List<Key> allKeys, Val prototype) throws CloneNotSupportedException {
        val = (Val) prototype.clone();
        val.setup(allKeys);
    }

    @Override
    public Val get(int index, List<Key> allKeys) {
        return val;
    }
}
