package minesweeper.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import common.board.Board;
import common.board.BoardImpl;
import common.board.ReadOnlyBoard;
import common.interfaces.Game;
import common.utils.IntVector2;
import common.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * MineSweeper game
 */
public class MineSweeper implements Game {

    private final ImmutableList<IntVector2> DIRECTIONS = ImmutableList.of(
            IntVector2.of(1, 1),
            IntVector2.of(1, 0),
            IntVector2.of(1, -1),
            IntVector2.of(0, 1),
            IntVector2.of(0, -1),
            IntVector2.of(-1, 1),
            IntVector2.of(-1, 0),
            IntVector2.of(-1, -1)
    );

    private Board<Cell> board;
    private Set<IntVector2> minePositions = ImmutableSet.of();

    private MineSweeper(int width, int height) {
        board = BoardImpl.make(width, height);
    }

    private void clearBoard() {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                board.set(Cell.EMPTY, x, y);
            }
        }
    }

    private void randomizeWithNumMines(int numMines) {
        clearBoard();

        List<IntVector2> boardPositions = new ArrayList<>();
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                boardPositions.add(IntVector2.of(x, y));
            }
        }

        minePositions = ImmutableSet.copyOf(RandomUtils.randomSubset(boardPositions, numMines));
    }

    private Cell calculateCell(IntVector2 position) {
        if (minePositions.contains(position)) {
            return Cell.BOMB;
        }
        //TODO: this is messed up, calculating incorrect numbers
        int numOfTouchingBombs = DIRECTIONS.stream().mapToInt(direction -> minePositions.contains(position.add(direction)) ? 1 : 0).sum();
        return Cell.cellNumMap.inverse().get(numOfTouchingBombs);
    }


    @SuppressWarnings("SameParameterValue")
    public static MineSweeper create(int width, int height, int mines) {
        checkArgument(mines < width * height, "cannot have more mines than grid spaces");
        MineSweeper game = new MineSweeper(width, height);
        game.randomizeWithNumMines(mines);
        return game;
    }

    public void move(IntVector2 pos) {
        checkArgument(!isComplete(), "game complete, no further moves allowed");
        revealLocation(pos);
    }

    private void revealLocation(IntVector2 pos) {
        if (!board.validPos(pos) || board.get(pos) != Cell.EMPTY) {
            return; // this cell has already been revealed
        }
        Cell cellVal = calculateCell(pos);
        board.set(cellVal, pos);
        if (cellVal == Cell.N0) {
            DIRECTIONS.forEach(direction -> revealLocation(pos.add(direction)));
        }
    }

    private boolean unrevealedMineHere(IntVector2 pos) {
        return board.get(pos).equals(Cell.EMPTY) && minePositions.contains(pos);
    }

    public boolean hasWon() {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                IntVector2 pos = IntVector2.of(x, y);
                if (board.get(pos) == Cell.BOMB || !unrevealedMineHere(pos) && !board.get(pos).finalState) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean hasLost() {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                IntVector2 pos = IntVector2.of(x, y);
                if (board.get(pos) == Cell.BOMB) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isComplete() {
        return hasWon() || hasLost();
    }

    public ReadOnlyBoard<Cell> getBoard() {
        return board;
    }
}
