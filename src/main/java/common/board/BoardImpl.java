package common.board;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * Game Board
 */
public class BoardImpl<T> implements Board<T> {

    private final T board[][];

    private BoardImpl(int width, int height) {
        checkArgument(width > 0, "Width must be greater than 0");
        checkArgument(height > 0, "Height must be greater than 0");
        //noinspection unchecked
        board = (T[][]) new Object[width][height];
    }

    public static <T> Board<T> make(int width, int height) {
        return new BoardImpl<>(width, height);
    }

    /**
     * Creates a shallow copy of the board using board.get to retrieve the cells.
     *
     * @param board board to copy
     * @param <T>   cell type
     * @return duplicate of board.
     */
    public static <T> Board<T> copyOf(ReadOnlyBoard<T> board) {
        Board<T> duplicatedBoard = new BoardImpl<>(board.getWidth(), board.getHeight());
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                duplicatedBoard.set(board.get(x, y), x, y);
            }
        }
        return duplicatedBoard;
    }

    /**
     * @throws IllegalArgumentException if position is invalid
     */
    private void validatePosition(int x, int y) {
        checkArgument(0 <= x, "x must be positive");
        checkArgument(0 <= y, "y must be positive");
        checkArgument(x < getWidth(), "x must be less than " + getWidth());
        checkArgument(y < getHeight(), "x must be less than " + getHeight());
    }

    @Override
    public T get(int x, int y) {
        validatePosition(x, y);
        return board[x][y];
    }

    @Override
    public int getWidth() {
        return board.length;
    }

    @Override
    public int getHeight() {
        checkState(board.length > 0, "Board length expected to be > 0");
        return board[0].length;
    }

    @Override
    public void set(T block, int x, int y) {
        validatePosition(x, y);
        board[x][y] = block;
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
