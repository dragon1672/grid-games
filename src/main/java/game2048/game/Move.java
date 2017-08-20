package game2048.game;

import common.utils.IntVector2;

public enum Move {
    UP(IntVector2.of(0, 1)),
    DOWN(IntVector2.of(0, -1)),
    LEFT(IntVector2.of(-1, 0)),
    RIGHT(IntVector2.of(1, 0));

    final IntVector2 dir;

    Move(IntVector2 dir) {
        this.dir = dir;
    }
}
