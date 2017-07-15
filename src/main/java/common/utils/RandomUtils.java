package common.utils;

import java.util.List;
import java.util.Random;

import static com.google.common.base.Preconditions.checkArgument;

public class RandomUtils {

    private static final Random rand = new Random();

    private static <T> void swap(List<T> list, int left, int right) {
        T tmp = list.get(left);
        list.set(left, list.get(right));
        list.set(right, tmp);
    }

    public static <T> List<T> randomSubset(List<T> mutableSource, int count) {
        checkArgument(count < mutableSource.size());
        for (int i = 0; i < count; i++) {
            int randomIndex = rand.nextInt(mutableSource.size());
            swap(mutableSource, i, randomIndex);
        }
        return mutableSource.subList(0, count);
    }

    public static <T> T randomFromList(List<T> list) {
        return list.get(rand.nextInt(list.size()));
    }
}
