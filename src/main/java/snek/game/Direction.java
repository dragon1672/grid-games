package snek.game;

import common.utils.IntVector2;

public enum Direction {
    UP(IntVector2.of(0, 1)),
    DOWN(IntVector2.of(0, -1)),
    RIGHT(IntVector2.of(1, 0)),
    LEFT(IntVector2.of(-1, 0));

    public final IntVector2 vector;

    Direction(IntVector2 vector) {
        this.vector = vector;
    }

    public static Direction fromVector(IntVector2 vector) {
        for (Direction dir : values()) {
            if (dir.vector.equals(vector)) {
                return dir;
            }
        }
        throw new IllegalArgumentException("Invalid vector");
    }
}
