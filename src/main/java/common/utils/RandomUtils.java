package common.utils;

import com.google.common.annotations.VisibleForTesting;

import java.util.*;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;

public class RandomUtils {
    @VisibleForTesting
    static Random rand = new Random();

    private static <T> void swap(List<T> list, int left, int right) {
        T tmp = list.get(left);
        list.set(left, list.get(right));
        list.set(right, tmp);
    }

    public static <T> List<T> randomSubset(List<T> source, int count) {
        checkArgument(count <= source.size());
        List<T> mutableSource = new ArrayList<>(source);
        for (int i = 0; i < count; i++) {
            int randomIndex = rand.nextInt(mutableSource.size());
            swap(mutableSource, i, randomIndex);
        }
        return mutableSource.subList(0, count);
    }

    @SafeVarargs
    public static <T> T randomFromList(T... list) {
        return list[rand.nextInt(list.length)];
    }

    public static <T> T randomFromList(List<T> list) {
        return list.get(rand.nextInt(list.size()));
    }

    public static <T> Stream<T> randomizeStream(Stream<T> stream) {
        return stream.sorted(new RandomComparator<>(rand));
    }

    private static class RandomComparator<T> implements Comparator<T> {
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
}
