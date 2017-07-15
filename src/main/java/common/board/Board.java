package common.board;

import common.utils.IntVector2;

/**
 * Read Write game board
 */
public interface Board<T> extends ReadOnlyBoard<T> {

    default void set(T block, IntVector2 pos) {
        set(block, pos.x, pos.y);
    }

    void set(T block, int x, int y);
}
