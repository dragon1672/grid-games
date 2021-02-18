package minesweeper.game;

import com.google.common.collect.ImmutableSet;
import com.sun.istack.internal.Nullable;
import common.board.Board;
import common.board.BoardImpl;
import common.board.ReadOnlyBoard;
import common.utils.BoardUtils;
import common.utils.IntVector2;
import common.utils.RandomUtils;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;

class MineSweeperGameGenerated extends MineSweeper {
    @Nullable
    public MineSweeperGameFromBombs game = null;
    private final int width;
    private final int height;
    private final ReadOnlyBoard<Cell> emptyBoard;
    private final int numMines;

    MineSweeperGameGenerated(int width, int height, int numMines) {
        checkArgument(numMines > 0, "Must have more than 0 mines");
        checkArgument(numMines < (width * height - DIRECTIONS.size() + 1), "cannot have more mines than grid spaces");
        this.width = width;
        this.height = height;
        emptyBoard = BoardImpl.make(width, height, Cell.EMPTY);
        this.numMines = numMines;
    }

    private static MineSweeperGameFromBombs generateWithBlacklistPositions(int width, int height, int numMines, ImmutableSet<IntVector2> blacklistedPositions) {
        Board<Cell> board = BoardImpl.make(width, height, Cell.EMPTY);
        List<IntVector2> possibleMineLocations = BoardUtils.boardPositionsAsStream(board).filter(pos -> !blacklistedPositions.contains(pos)).collect(toImmutableList());
        ImmutableSet<IntVector2> minePositions = ImmutableSet.copyOf(RandomUtils.randomSubset(possibleMineLocations, numMines));

        return new MineSweeperGameFromBombs(width, height, minePositions);
    }

    private void generateGameIfUnset(IntVector2 revealPos) {
        if (game == null) {
            ImmutableSet.Builder<IntVector2> blackListedLocations = ImmutableSet.builder();
            blackListedLocations.add(revealPos);
            DIRECTIONS.stream().map(revealPos::add).forEach(blackListedLocations::add);
            game = generateWithBlacklistPositions(width, height, numMines, blackListedLocations.build());
        }
    }

    @Override
    public void move(IntVector2 move) {
        generateGameIfUnset(move);
        assert game != null;
        game.move(move);
    }

    @Override
    public boolean hasWon() {
        if (game != null) {
            return game.hasWon();
        }
        return false;
    }

    @Override
    public boolean hasLost() {
        if (game != null) {
            return game.hasLost();
        }
        return false;
    }

    @Override
    public void revealAllBombs() {
        if (game != null) {
            game.revealAllBombs();
        }
    }

    @Override
    public int getMineCount() {
        return numMines;
    }

    @Override
    public boolean isComplete() {
        if (game != null) {
            return game.isComplete();
        }
        return false;
    }

    @Override
    public ReadOnlyBoard<Cell> getBoard() {
        if (game != null) {
            return game.getBoard();
        }
        return emptyBoard;
    }
}
