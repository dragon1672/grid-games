package conway_lyfe.game;

import com.google.common.collect.ImmutableSet;
import common.utils.IntVector2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ConwayBoard implements ConwayReadOnlyBoard {
    private static final ImmutableSet<IntVector2> neighbors = ImmutableSet.of(
            IntVector2.of(-1, -1),
            IntVector2.of(-1, 0),
            IntVector2.of(-1, 1),
            IntVector2.of(0, -1),
            //IntVector2.of(0,0),
            IntVector2.of(0, 1),
            IntVector2.of(1, -1),
            IntVector2.of(1, 0),
            IntVector2.of(1, 1)
    );

    private static boolean shouldLive(boolean isAlive, int neighborCount) {
        if (isAlive) {
            return neighborCount == 2 || neighborCount == 3;
        } else {
            return neighborCount == 3;
        }
    }

    final private ImmutableSet<IntVector2> points;

    private ConwayBoard(ImmutableSet<IntVector2> points) {
        this.points = points;
    }

    @Override
    public boolean isActive(IntVector2 pos) {
        return points.contains(pos);
    }

    @Override
    public Iterable<IntVector2> getPoints() {
        return points;
    }

    /**
     * Returns a new board with the next state.
     */
    public ConwayBoard evolve() {
        /*
        Any live cell with fewer than two live neighbours dies, as if by underpopulation.
        Any live cell with two or three live neighbours lives on to the next generation.
        Any live cell with more than three live neighbours dies, as if by overpopulation.
        Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.
         */
        Map<IntVector2, Integer> neighborCount = new HashMap<>();
        Builder nextGen = new Builder();

        // Might be opportunity here to shard to speed up performance
        for (IntVector2 cell : points) {
            for (IntVector2 offset : neighbors) {
                IntVector2 pos = cell.add(offset);
                int count = neighborCount.getOrDefault(pos, 0) + 1;
                neighborCount.put(pos, count);
                nextGen.setState(pos, shouldLive(isActive(pos), count));
            }
        }
        return nextGen.build();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return asString();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Builder implements ConwayReadOnlyBoard {
        private final HashSet<IntVector2> points = new HashSet<>();

        private Builder() {
        }

        public Builder setState(IntVector2 pos, boolean alive) {
            if (alive) {
                setActive(pos);
            } else {
                setInactive(pos);
            }
            return this;
        }

        public Builder setActive(IntVector2 pos) {
            points.add(pos);
            return this;
        }

        public Builder setActive(IntVector2... points) {
            for (IntVector2 pos : points) {
                setActive(pos);
            }
            return this;
        }

        public Builder setInactive(IntVector2 pos) {
            points.remove(pos);
            return this;
        }

        @Override
        public boolean isActive(IntVector2 pos) {
            return points.contains(pos);
        }

        @Override
        public Iterable<IntVector2> getPoints() {
            return points;
        }

        public ConwayBoard build() {
            return new ConwayBoard(ImmutableSet.copyOf(points));
        }

        @Override
        public String toString() {
            return asString();
        }
    }

}
