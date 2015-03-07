package utils.flyweight;

import utils.Prototype;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Marek on 21.02.2015.
 * Node of buffer
 */
public class FlyweightMap<Key, Val extends Prototype> implements Flyweight<Key,Val> {
    private final Val prototype;
    HashMap<Key, Flyweight<Key, Val>> buffer = new HashMap<>();

    private FlyweightMap(int index, List<Key> allKeys, Val prototype) throws CloneNotSupportedException {
        this.prototype = prototype;
        populateBuffer(index, allKeys);
    }

    public FlyweightMap(Val prototype) {
        this.prototype = prototype;
    }

    @Override
    public Val get(int index, List<Key> allKeys) throws CloneNotSupportedException {
        Key keyVal = allKeys.get(index++);

        if (!buffer.containsKey(keyVal))
            populateBuffer(index - 1, allKeys);

        return buffer.get(keyVal).get(index, allKeys);
    }

    private void populateBuffer(int index, List<Key> allKeys) throws CloneNotSupportedException {
        Key keyVal = allKeys.get(index++);
        Flyweight<Key, Val> f;
        if (index < allKeys.size()) {
            f = new FlyweightMap<>(index, allKeys, prototype);
        } else {
            f = new FlyweightLeaf<>(allKeys, prototype);
        }
        buffer.put(keyVal, f);
    }

//    @Override
//    public Val get(ListIterator<Key> key, List<Key> p) throws CloneNotSupportedException {
//        return get(p.listIterator(), p);
//    }
}
