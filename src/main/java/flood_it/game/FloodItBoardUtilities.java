package flood_it.game;

import common.board.ReadOnlyBoard;
import common.utils.BoardUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Helpful Generic Board Utilities
 */
public class FloodItBoardUtilities {
    public static List<FloodColor> movesOnBoard(ReadOnlyBoard<FloodColor> board) {
        return BoardUtils.cellTypesOnBoard(board).stream() // get the cells on the board
                .filter(cell -> cell != board.get(FloodIt.MOVE_POS)) // remove the currently selected color
                .collect(Collectors.toList());
    }

    public static <T> ReadOnlyBoard<T> simulateFillMove(ReadOnlyBoard<T> board, T newBlockColor) {
        return BoardUtils.replaceConnectedCells(board, newBlockColor, FloodIt.MOVE_POS);
    }
}
