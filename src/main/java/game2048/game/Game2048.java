package game2048.game;

import com.google.common.collect.ImmutableSet;
import common.board.Board;
import common.board.BoardImpl;
import common.board.ReadOnlyBoard;
import common.interfaces.Game;
import common.utils.BoardUtils;
import common.utils.IntVector2;

import java.util.Set;

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
        //TODO make this not sloppy and export to utils

        //TODO fix bug where combinations should happen from the bottom up
        /*
            taking

            1
            111
            1 1

            Moving down should result in

            1
            212

            This implementation could create

            2   // the top merged before the bottom :(
            112
         */
        final boolean[] somethingMoved = new boolean[1];
        do {
            somethingMoved[0] = false;
            BoardUtils.boardPositionsAsStream(board).forEach(pos -> {
                IntVector2 shiftedPos = pos.add(move.dir);
                if (board.validPos(shiftedPos) && board.get(pos) != Cell.N0) {
                    Cell currentCell = board.get(pos);
                    if (board.get(shiftedPos) == Cell.N0) {
                        board.set(currentCell, shiftedPos); // copy into empty space
                        board.set(Cell.N0, pos); // remove from old position
                        somethingMoved[0] = true;
                    } else if (board.get(shiftedPos) == currentCell) {
                        // We need combine stuff
                        Cell nextLevel = getNextLevel(currentCell);
                        board.set(nextLevel, shiftedPos);
                        board.set(Cell.N0, pos); // remove from old position
                        somethingMoved[0] = true;
                    }
                }
            });
        } while (somethingMoved[0]);

        addRandomSquare(1);
    }

    private Set<Move> getMoves() {
        return ImmutableSet.copyOf(Move.values()); // TODO restrict based off actual moves
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
