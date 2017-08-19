package common.utils;

import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Random;

public class RandomComparator<T> implements Comparator<T> {
    private final Random rand;
    private final Map<T, Integer> cache = new IdentityHashMap<>();

    RandomComparator(Random rand) {
        this.rand = rand;
    }

    private int getVal(T val) {
        return cache.computeIfAbsent(val, ignored -> rand.nextInt());
    }

    @Override
    public int compare(T o1, T o2) {
        return Integer.compare(getVal(o1), getVal(o2));
    }
}
