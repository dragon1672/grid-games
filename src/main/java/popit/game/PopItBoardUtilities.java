package popit.game;

import common.board.Board;
import common.board.BoardImpl;
import common.board.ReadOnlyBoard;
import common.board.SimulatedBoard;
import common.utils.BoardUtils;
import common.utils.IntVector2;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PopItBoardUtilities {
    public static ReadOnlyBoard<BlockColor> removeCells(ReadOnlyBoard<BlockColor> board, Set<IntVector2> positionsToRemove) {
        Map<IntVector2, BlockColor> overrides = positionsToRemove.stream()
                .collect(Collectors.toMap(pos -> pos, pos -> BlockColor.WHITES_INVALID));
        return shuffleCells(new SimulatedBoard<>(board, overrides), BlockColor.WHITES_INVALID);
    }

    private static ReadOnlyBoard<BlockColor> shuffleCells(ReadOnlyBoard<BlockColor> board, BlockColor emptySpace) {
        Board<BlockColor> boardWithMove = BoardImpl.make(board.getWidth(), board.getHeight());
        int retXIndex = 0;
        for (int x = 0; x < board.getWidth(); x++) {
            int retYIndex = board.getHeight() - 1;
            boolean emptyColumn = true;
            for (int y = board.getHeight() - 1; y >= 0; y--) {
                IntVector2 pos = IntVector2.of(x, y);
                if (board.get(pos) != emptySpace) {
                    emptyColumn = false;
                    boardWithMove.set(board.get(pos), retXIndex, retYIndex--);
                }
            }
            while (retYIndex >= 0) {
                boardWithMove.set(emptySpace, retXIndex, retYIndex--);
            }
            if (!emptyColumn) {
                retXIndex++;
            }
        }
        while (retXIndex < board.getWidth()) {
            for (int y = 0; y < board.getHeight(); y++) {
                boardWithMove.set(emptySpace, retXIndex, y);
            }
            retXIndex++;
        }
        return boardWithMove;
    }

    public static boolean isEmpty(ReadOnlyBoard<BlockColor> boardInstance) {
        return BoardUtils.isUniformColor(boardInstance) && boardInstance.get(0, 0) == BlockColor.WHITES_INVALID;
    }
}
