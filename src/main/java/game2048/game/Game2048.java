package game2048.game;

import common.board.Board;
import common.board.BoardImpl;
import common.board.ReadOnlyBoard;
import common.interfaces.Game;
import common.utils.BoardUtils;
import common.utils.IntVector2;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

/**
 * 2048 game
 */
public class Game2048 implements Game<Cell> {

    private Board<Cell> board;

    private Game2048(int width, int height) {
        board = BoardImpl.make(width, height);
        clearBoard();
        addRandomSquare(2);
    }

    private void clearBoard() {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                board.set(Cell.N0, x, y);
            }
        }
    }

    public static Game2048 create(int width, int height) {
        return new Game2048(width, height);
    }

    private void addRandomSquare(int numToAdd) {
        BoardUtils.boardPositionsAsRandomStream(board)
                .filter(pos -> board.get(pos) == Cell.N0)
                .limit(numToAdd)
                .forEach(pos -> board.set(Cell.N1, pos));
    }

    Cell getNextLevel(Cell current) {
        return Cell.cellNumMap.inverse().get(current.value + 1);
    }

    public void move(Move move) {
        Utils2048.mergeCells(board, move.dir);

        addRandomSquare(1);

    }

    private boolean canMove(Move move) {
        // TODO optimize this
        Set<IntVector2> positions = BoardUtils.boardPositionsAsStream(board).collect(Collectors.toSet());
        for (IntVector2 pos : positions) {
            IntVector2 shiftedPos = pos.add(move.dir);
            if (board.isValidPos(shiftedPos) && board.get(pos) != Cell.N0) {
                Cell currentCell = board.get(pos);
                if (board.get(shiftedPos) == Cell.N0) {
                    return true;
                } else if (board.get(shiftedPos) == currentCell) {
                    return true;
                }
            }
        }
        return false;
    }

    private Set<Move> getMoves() {
        return Stream.of(Move.values()).filter(this::canMove).collect(toImmutableSet());
    }

    @Override
    public boolean isComplete() {
        return getMoves().isEmpty();
    }

    @Override
    public ReadOnlyBoard<Cell> getBoard() {
        return board;
    }
}
