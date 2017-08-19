package common.board;

import common.utils.AsciiBoard;
import common.utils.IntVector2;

import java.util.Objects;

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

    default boolean isEqualTo(ReadOnlyBoard that) {
        if (this.getWidth() != that.getWidth() || this.getHeight() != that.getHeight()) {
            return false;
        }

        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                if (!Objects.equals(this.get(x, y), that.get(x, y))) {
                    return false;
                }
            }
        }
        return true;
    }

    default int getHashCode() {
        int result = 1;

        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                result = 31 * result + Objects.hashCode(get(x, y));
            }
        }
        return result;
    }

    default String asString() {
        return AsciiBoard.boardToString(this);
    }
}
