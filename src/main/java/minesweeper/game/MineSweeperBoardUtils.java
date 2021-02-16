package minesweeper.game;

import common.board.ReadOnlyBoard;
import common.utils.BoardUtils;
import common.utils.IntVector2;

import java.util.List;

import static com.google.common.collect.ImmutableList.toImmutableList;

@SuppressWarnings("UnstableApiUsage")
public class MineSweeperBoardUtils {
    public static List<IntVector2> getMoves(ReadOnlyBoard<Cell> board) {
        return BoardUtils.boardPositionsAsStream(board).filter(pos -> !board.get(pos).finalState).collect(toImmutableList());
    }

    public static List<IntVector2> getShownNumbers(ReadOnlyBoard<Cell> board) {
        return BoardUtils.boardPositionsAsStream(board).filter(pos -> board.get(pos).numAdjacentBombs > 0).collect(toImmutableList());
    }

    public static boolean isClear(ReadOnlyBoard<Cell> board) {
        return BoardUtils.boardPositionsAsStream(board).map(board::get).allMatch(Cell.EMPTY::equals);
    }
}
