package common.board;

import com.google.common.collect.ImmutableMap;
import common.utils.IntVector2;

import java.util.Map;

/**
 * Board with set overrides
 */
public class SimulatedBoard<T> implements ReadOnlyBoard<T> {

    private final ReadOnlyBoard<T> baseBoard;
    private final ImmutableMap<IntVector2, T> overrides;

    public SimulatedBoard(ReadOnlyBoard<T> baseBoard, Map<IntVector2, T> overrides) {
        this.baseBoard = baseBoard;
        this.overrides = ImmutableMap.copyOf(overrides);
    }

    @Override
    public T get(int x, int y) {
        return overrides.getOrDefault(IntVector2.of(x, y), baseBoard.get(x, y));
    }

    @Override
    public int getWidth() {
        return baseBoard.getWidth();
    }

    @Override
    public int getHeight() {
        return baseBoard.getHeight();
    }

    @Override
    public boolean equals(Object that) {
        return this == that || that instanceof ReadOnlyBoard && isEqualTo((ReadOnlyBoard<?>) that);
    }

    @Override
    public int hashCode() {
        return getHashCode();
    }

    @Override
    public String toString() {
        return asString();
    }
}
