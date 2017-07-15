package common.board;

import common.utils.IntVector2;

/**
 * Read only game board
 */
public interface ReadOnlyBoard<T> {

    T get(int x, int y);

    int getWidth();

    int getHeight();

    default T get(IntVector2 pos) {
        return get(pos.x, pos.y);
    }

    default boolean validPos(int x, int y) {
        return 0 <= x && x < getWidth()
                && 0 <= y && y < getHeight();
    }

    default boolean validPos(IntVector2 pos) {
        return validPos(pos.x, pos.y);
    }
}
