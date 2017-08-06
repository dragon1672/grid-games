package lights_out.ai;

import common.board.Board;
import common.board.BoardImpl;
import common.board.ReadOnlyBoard;
import common.gui.BoardGui;
import common.utils.BoardUtils;
import common.utils.IntVector2;
import common.utils.SetOperations;
import lights_out.game.LightsOutBoardUtils;
import lights_out.game.LightsOutCell;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

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
        /*
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
        /*/

        // This appears to be causing a "Exception in thread "AWT-EventQueue-0" java.lang.NullPointerException" but I'm not sure why
        Optional<Set<IntVector2>> winningMoves = SetOperations.powerSet(BoardUtils.boardPositions(board))
                // process result and convert to an optional for if we find a winning move
                .map((Function<Set<IntVector2>, Optional<Set<IntVector2>>>) possibleMove -> {
                    ReadOnlyBoard<LightsOutCell> resultingBoard = getBoardWithMoves(board, possibleMove);
                    gui.updateBoard(resultingBoard);
                    if (BoardUtils.isUniformColor(resultingBoard)) {
                        return Optional.of(possibleMove);
                    }
                    return Optional.empty();
                })
                // Flattening Optional
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        return winningMoves.orElseThrow(() -> new IllegalArgumentException("board unsolvable"));
        //*/
    }
}
