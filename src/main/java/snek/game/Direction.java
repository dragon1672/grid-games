package snek.game;

import common.utils.IntVector2;

public enum Direction {
    UP(IntVector2.of(0, 1)),
    DOWN(IntVector2.of(0, -1)),
    RIGHT(IntVector2.of(1, 0)),
    LEFT(IntVector2.of(-1, 0));

    public final IntVector2 dir;

    Direction(IntVector2 dir) {
        this.dir = dir;
    }
}
