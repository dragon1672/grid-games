package minesweeper.game;

import com.google.common.collect.ImmutableList;
import common.board.ReadOnlyBoard;
import common.utils.BoardUtils;
import common.utils.IntVector2;

import java.util.List;

public class MineSweeperBoardUtils {
    public static List<IntVector2> getMoves(ReadOnlyBoard<Cell> board) {
        return BoardUtils.boardPositionsAsStream(board).filter(pos -> !board.get(pos).finalState).collect(ImmutableList.toImmutableList());
    }

    public static List<IntVector2> getShownNumbers(ReadOnlyBoard<Cell> board) {
        return BoardUtils.boardPositionsAsStream(board).filter(pos -> board.get(pos).numAdjacentBombs > 0).collect(ImmutableList.toImmutableList());
    }
}
