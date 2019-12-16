package minesweeper.game;

import com.google.common.collect.ImmutableSet;
import common.board.Board;
import common.board.BoardImpl;
import common.board.ReadOnlyBoard;
import common.utils.IntVector2;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

class MineSweeperGameFromBombs extends MineSweeper {

    private Board<Cell> board;
    private final ImmutableSet<IntVector2> minePositions;

    MineSweeperGameFromBombs(int width, int height, ImmutableSet<IntVector2> minePositions) {
        board = BoardImpl.make(width, height, Cell.EMPTY);
        List<IntVector2> invalidPositions = minePositions.stream().filter(bombPos -> !board.isValidPos(bombPos)).collect(Collectors.toList());
        checkArgument(invalidPositions.isEmpty(), "bomb positions must fit on %d x %d board, %d did not", width, height, invalidPositions.toArray());

        this.minePositions = minePositions;
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

    public void move(IntVector2 pos) {
        checkArgument(!isComplete(), "game complete, no further moves allowed");
        revealLocation(pos);
    }

    private void revealLocation(IntVector2 pos) {
        if (!board.isValidPos(pos) || board.get(pos) != Cell.EMPTY) {
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
        // Check the board to see if all non bombs have been revealed
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
        // Check the board to see if a bomb has been revealed
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

    public void revealAllBombs() {
        minePositions.forEach(this::revealLocation);
    }

    @Override
    public int getMineCount() {
        return minePositions.size();
    }

    @Override
    public ReadOnlyBoard<Cell> getBoard() {
        return board;
    }

}
