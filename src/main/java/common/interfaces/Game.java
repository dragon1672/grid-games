package common.interfaces;

import common.board.ReadOnlyBoard;

/**
 * Generic interface that applies to all games
 */
public interface Game<T> {
    boolean isComplete();

    ReadOnlyBoard<T> getBoard();
}
