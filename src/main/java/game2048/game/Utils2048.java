package game2048.game;

import common.board.Board;
import common.utils.BoardUtils;
import common.utils.IntVector2;

public class Utils2048 {

    public static Cell getNextLevel(Cell current) {
        return Cell.cellNumMap.inverse().get(current.value + 1);
    }

    public static void mergeCells(Board<Cell> board, Move dir) {
        mergeCells(board, dir.dir);
    }

    public static void mergeCells(Board<Cell> board, IntVector2 dir) {
        final boolean[] somethingMoved = new boolean[1];
        do {
            somethingMoved[0] = false;
            BoardUtils.boardPositionsAsStream(board).forEach(pos -> {
                IntVector2 shiftedPos = pos.add(dir);
                if (board.isValidPos(shiftedPos) && board.get(pos) != Cell.N0) {
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
    }
}
