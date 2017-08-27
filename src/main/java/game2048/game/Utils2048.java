package game2048.game;

import common.board.Board;
import common.utils.BoardUtils;
import common.utils.IntVector2;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static common.utils.IntVector2.iVec;

public class Utils2048 {

    public static Cell getNextLevel(Cell current) {
        return Cell.cellNumMap.inverse().get(current.value + 1);
    }

    public static void mergeCells(Board<Cell> board, Move dir) {
        mergeCells(board, dir.dir);
    }
    public static void mergeCells(Board<Cell> board, IntVector2 dir) {
        mergeOldIdea(board, dir);
    }

    private static void mergeNewIdea(Board<Cell> board, IntVector2 dir) {
        checkArgument((Math.abs(dir.x) == 1) != (Math.abs(dir.y) == 1), "dir:  %s expected to have length of 1", dir);
        // Flip x and y
        final IntVector2 nextSectionDir = iVec(Math.abs(dir.y), Math.abs(dir.x));

        // Use the dir to only get an x or y component, and start at either 0 or width/height
        IntVector2 startingSectionPos = iVec(Math.max(0, dir.x * board.getWidth() - 1), Math.max(0, dir.y * board.getHeight() - 1));

        while (board.isValidPos(startingSectionPos)) {
            IntVector2 currentPos = startingSectionPos.sub(dir); // no need to check the edge
            IntVector2 lastWhiteSpaceOffset = dir;
            while (board.isValidPos(currentPos)) {
                IntVector2 lastWhiteSpacePos = currentPos.add(lastWhiteSpaceOffset);
                checkState(!currentPos.equals(lastWhiteSpacePos));
                // shuffle
                if (board.get(currentPos) != Cell.N0) {
                    board.set(board.get(lastWhiteSpacePos), currentPos);
                    board.set(Cell.N0, currentPos);
                } else if (board.get(lastWhiteSpacePos) == Cell.N0) {
                    lastWhiteSpaceOffset = lastWhiteSpaceOffset.add(dir);
                }

                // check for merge
                IntVector2 nextSpot = currentPos.add(dir);
                if (board.isValidPos(nextSpot) && board.get(currentPos) == board.get(nextSpot)) {
                    Cell nextLevel = getNextLevel(board.get(nextSpot));
                    board.set(nextLevel, nextSpot);
                    board.set(Cell.N0, currentPos);
                    lastWhiteSpaceOffset = lastWhiteSpaceOffset.add(dir);
                }
                currentPos = currentPos.sub(dir);
            }
            startingSectionPos = startingSectionPos.sub(nextSectionDir);
        }
    }

    private static void mergeOldIdea(Board<Cell> board, IntVector2 dir) {
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
