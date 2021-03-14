package nonogram;

import java.util.HashSet;

public class AssignableSet<T> extends HashSet<T> {
    public void updateContains(T value, boolean shouldBeContained) {
        if (contains(value) && !shouldBeContained) {
            remove(value);
        }
        if (!contains(value) && shouldBeContained) {
            add(value);
        }
    }
}
