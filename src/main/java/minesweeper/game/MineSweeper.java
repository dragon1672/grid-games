package minesweeper.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import common.board.Board;
import common.board.BoardImpl;
import common.board.ReadOnlyBoard;
import common.interfaces.Game;
import common.utils.BoardUtils;
import common.utils.IntVector2;
import common.utils.RandomUtils;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * MineSweeper game
 */
public class MineSweeper implements Game<Cell> {

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
    private final int numMines;

    private MineSweeper(int width, int height, int numMines) {
        checkArgument(numMines > 0, "Must have more than 0 mines");
        checkArgument(numMines < width * height, "cannot have more mines than grid spaces");
        this.numMines = numMines;
        board = BoardImpl.make(width, height);
        clearBoard();
    }

    private void clearBoard() {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                board.set(Cell.EMPTY, x, y);
            }
        }
    }

    private boolean minesAreUnSet() {
        return minePositions.isEmpty();
    }

    private void initMinesIfUnset(IntVector2 blacklistedLocation) {
        if (minesAreUnSet()) {
            placeMines(blacklistedLocation);
        }
    }

    private void placeMines(IntVector2 blackListedLocation) {
        clearBoard();

        List<IntVector2> possibleMineLocations = BoardUtils.boardPositionsAsStream(board).filter(pos -> !pos.equals(blackListedLocation)).collect(toImmutableList());

        minePositions = ImmutableSet.copyOf(RandomUtils.randomSubset(possibleMineLocations, numMines));
    }

    private Cell calculateCell(IntVector2 position) {
        if (minePositions.contains(position)) {
            return Cell.BOMB;
        }
        int numOfTouchingBombs = DIRECTIONS.stream()
                .map(position::add) // map to neighbor location
                .map(minePositions::contains) // check if bomb
                .mapToInt(neighborHasBomb -> neighborHasBomb ? 1 : 0)
                .sum();
        return Cell.cellNumMap.inverse().get(numOfTouchingBombs);
    }


    public static MineSweeper create(int width, int height, int mines) {
        return new MineSweeper(width, height, mines);
    }

    public void move(IntVector2 pos) {
        checkArgument(!isComplete(), "game complete, no further moves allowed");
        revealLocation(pos);
    }

    private void revealLocation(IntVector2 pos) {
        initMinesIfUnset(pos);
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
        if (minesAreUnSet()) {
            return false;
        }
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
        if (minesAreUnSet()) {
            return false;
        }
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

    public void revealAll() {
        List<IntVector2> possibleMoves = MineSweeperBoardUtils.getMoves(board);
        while (!possibleMoves.isEmpty()) {
            IntVector2 positionToReveal = possibleMoves.get(0);
            revealLocation(positionToReveal);
            possibleMoves = MineSweeperBoardUtils.getMoves(board);
        }
    }

    @Override
    public boolean isComplete() {
        return hasWon() || hasLost();
    }

    @Override
    public ReadOnlyBoard<Cell> getBoard() {
        return board;
    }
}
