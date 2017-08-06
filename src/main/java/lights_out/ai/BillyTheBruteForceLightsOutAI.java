package lights_out.ai;

import com.google.common.collect.Sets;
import common.board.Board;
import common.board.BoardImpl;
import common.board.ReadOnlyBoard;
import common.gui.BoardGui;
import common.utils.BoardUtils;
import common.utils.IntVector2;
import lights_out.game.LightsOutBoardUtils;
import lights_out.game.LightsOutCell;

import java.util.Set;

public class BillyTheBruteForceLightsOutAI implements LightsOutAI {
    private final BoardGui<LightsOutCell> gui;

    public BillyTheBruteForceLightsOutAI(BoardGui<LightsOutCell> gui) {
        this.gui = gui;
    }

    private ReadOnlyBoard<LightsOutCell> getBoardWithMoves(ReadOnlyBoard<LightsOutCell> originalBoard, Set<IntVector2> moves) {
        Board<LightsOutCell> mutableBoard = BoardImpl.copyOf(originalBoard);
        moves.forEach(move -> LightsOutBoardUtils.toggleSurroundingCells(mutableBoard, move));
        return mutableBoard;
    }

    @Override
    public Set<IntVector2> getMoves(ReadOnlyBoard<LightsOutCell> board) {
        // power set doesn't for for a set of over 30 elements, I'll need to make my own that can handle larger boards
        Set<Set<IntVector2>> allPossibleMoves = Sets.powerSet(BoardUtils.boardPositions(board));
        for (Set<IntVector2> possibleMoves : allPossibleMoves) {
            ReadOnlyBoard<LightsOutCell> resultingBoard = getBoardWithMoves(board, possibleMoves);
            gui.updateBoard(resultingBoard);
            if (BoardUtils.isUniformColor(resultingBoard)) {
                return possibleMoves;
            }
        }
        throw new IllegalArgumentException("board unsolvable");
    }
}
