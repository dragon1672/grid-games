package chess.game;

import com.google.common.collect.ImmutableList;
import common.board.ReadOnlyBoard;
import common.utils.IntVector2;
import minesweeper.game.*;

import java.util.List;

public class MineSweeperBoardUtils {
    public static List<IntVector2> getMoves(ReadOnlyBoard<minesweeper.game.Cell> board) {
        ImmutableList.Builder<IntVector2> moves = ImmutableList.builder();
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                IntVector2 pos = IntVector2.of(x, y);
                if (!board.get(pos).finalState) {
                    moves.add(pos);
                }
            }
        }
        return moves.build();
    }
}