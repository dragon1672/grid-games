package minesweeper.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import common.board.Board;
import common.board.BoardLoaders;
import common.interfaces.Game;
import common.utils.BoardUtils;
import common.utils.IntVector2;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

/**
 * MineSweeper game
 */
public abstract class MineSweeper implements Game<Cell> {
    public static final ImmutableList<IntVector2> DIRECTIONS = ImmutableList.of(
            IntVector2.of(1, 1),
            IntVector2.of(1, 0),
            IntVector2.of(1, -1),
            IntVector2.of(0, 1),
            IntVector2.of(0, -1),
            IntVector2.of(-1, 1),
            IntVector2.of(-1, 0),
            IntVector2.of(-1, -1)
    );

    public abstract void move(IntVector2 move);

    public abstract boolean hasWon();

    public abstract boolean hasLost();

    public abstract void revealAllBombs();

    public abstract int getMineCount();

    /**
     * Creates a minesweeper game that will generate bomb locations after the first move.
     */
    public static MineSweeper create(int width, int height, int mines) {
        return new MineSweeperGameGenerated(width, height, mines);
    }

    /**
     * Generate a board based off a string showing bomb locations.
     * String is expected to be formatted in a square
     *
     * @param str      board to be converted
     * @param bombChar char to be interpreted as a bomb
     */
    public static MineSweeper createFromString(String str, char bombChar) {
        Board<Cell> bombBoard = BoardLoaders.generateFromString(str, c -> c == bombChar ? Cell.BOMB : Cell.EMPTY);
        ImmutableSet<IntVector2> minePositions = BoardUtils.boardPositionsAsRandomStream(bombBoard).filter(pos -> bombBoard.get(pos) == Cell.BOMB).collect(toImmutableSet());
        return new MineSweeperGameFromBombs(bombBoard.getWidth(), bombBoard.getHeight(), minePositions);
    }
}
