package lights_out.game;

import common.board.Board;
import common.utils.IntVector2;

import java.util.stream.Stream;

public class LightsOutBoardUtils {
    public static void toggleSurroundingCells(Board<LightsOutCell> board, IntVector2 pos) {
        Stream.of(
                IntVector2.of(1, 0),
                IntVector2.of(-1, 0),
                IntVector2.of(0, 1),
                IntVector2.of(0, -1))
                .map(pos::add)
                .filter(board::isValidPos)
                .forEach(cellToToggle -> {
                    LightsOutCell cellToSet = board.get(cellToToggle) == LightsOutCell.TRUE ? LightsOutCell.FALSE : LightsOutCell.TRUE;
                    board.set(cellToSet, cellToToggle);
                });
    }
}
