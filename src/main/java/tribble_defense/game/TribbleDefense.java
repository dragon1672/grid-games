package tribble_defense.game;

import com.google.common.collect.ImmutableSet;
import common.board.Board;
import common.board.BoardImpl;
import common.board.ReadOnlyBoard;
import common.interfaces.Game;
import common.utils.BoardUtils;
import common.utils.IntVector2;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * TribbleDefense game
 */
public class TribbleDefense implements Game<Cell> {
    // TODO bunch of bugs
    // TODO generated blockers need to not create closed areas
    // TODO Allow hard coding initial board
    // TODO options with generating board
    // TODO Allow different max cell
    private Board<Cell> board;

    public static Cell getMoveCellForBoard(ReadOnlyBoard<Cell> board) {
        Cell minCellOnBoard = BoardUtils.boardPositionsAsStream(board).map(board::get).min(Comparator.comparingInt(c -> c.value)).orElse(Cell.N0);
        return Cell.cellNumMap.inverse().getOrDefault(minCellOnBoard.value - 1, Cell.N0);
    }

    public Cell getMoveCell() {
        return getMoveCellForBoard(board);
    }

    private TribbleDefense(int width, int height, int nubObstacles) {
        checkArgument(nubObstacles >= 0, "Cannot have negative obstacles");
        checkArgument(nubObstacles < width * height, "cannot have more obstacles than grid spaces");
        board = BoardImpl.make(width, height);
        clearBoard();
        addObstacles(nubObstacles);
        // TODO make this more legit
        board.set(Cell.N1, 0, 0);
    }

    private void clearBoard() {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                board.set(Cell.N0, x, y);
            }
        }
    }

    private void addObstacles(int num) {
        BoardUtils.boardPositionsAsRandomStream(board)
                .filter(pos -> board.get(pos) == Cell.N0)
                .limit(num)
                .forEach(pos -> board.set(Cell.BLOCKED, pos));
    }

    public static TribbleDefense create(int width, int height, int mines) {
        return new TribbleDefense(width, height, mines);
    }

    public void move(IntVector2 pos) {
        checkArgument(!isComplete(), "game complete, no further moves allowed");
        checkArgument(board.get(pos) == Cell.N0, "Can only place on empty square");

        board.set(getMoveCell(), pos);
        upgrade(pos);
    }

    private Optional<Cell> getNextProgression(Cell current) {
        return Optional.ofNullable(Cell.cellNumMap.inverse().get(current.value + 1));
    }

    private void upgrade(IntVector2 pos) {
        while (true) {
            Optional<Cell> nextCell = getNextProgression(board.get(pos));
            Set<IntVector2> positionsToUpgrade = BoardUtils.getConnectedCells(board, pos);

            if (positionsToUpgrade.size() < 3) {
                break;
            }

            positionsToUpgrade.forEach(posToUpgrade -> board.set(Cell.N0, posToUpgrade));
            board.set(nextCell.orElse(Cell.N0), pos);

            if (!nextCell.isPresent()) {
                break;
            }
        }
    }

    @Override
    public boolean isComplete() {
        return BoardUtils.boardPositionsAsStream(board).map(board::get).allMatch(cell -> ImmutableSet.of(Cell.N0, Cell.BLOCKED).contains(cell));
    }

    @Override
    public ReadOnlyBoard<Cell> getBoard() {
        return board;
    }
}
