package popit.ai.exhaustiveAIs;

import common.board.ReadOnlyBoard;
import common.gui.BoardGui;
import common.utils.IntVector2;
import popit.ai.AI;
import popit.game.BlockColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Explores all possible outcomes and returns the best solutions
 * Right now way to slow to be usable
 */
public class ExhaustiveIterativeAI extends AI {
    private final BoardGui<BlockColor> gui;

    public ExhaustiveIterativeAI(BoardGui<BlockColor> gui) {
        this.gui = gui;
    }

    private static List<IntVector2> extractMoves(MovePossibility movePossibility) {
        List<IntVector2> moves = new ArrayList<>();
        extractMoves(movePossibility, moves);
        return moves;
    }

    private static void extractMoves(MovePossibility movePossibility, List<IntVector2> moves) {
        if (movePossibility.parent != null) {
            extractMoves(movePossibility.parent, moves);
        }
        moves.add(movePossibility.moveToMake);
    }

    @Override
    public List<IntVector2> getMoves(ReadOnlyBoard<BlockColor> originalBoard) {
        Stack<MovePossibility> possibleMoves = new Stack<>();

        getAllPossibleMoves(originalBoard).stream()
                .map(move -> new MovePossibility(originalBoard, move))
                .forEach(possibleMoves::add);

        MovePossibility bestGame = null;

        while (!possibleMoves.isEmpty()) {
            MovePossibility possibleMove = possibleMoves.pop();
            gui.updateBoard(possibleMove.boardInstance);

            if (possibleMove.isComplete() || AI.unWinnable(possibleMove.boardInstance)) {
                if (possibleMove.isComplete() && (bestGame == null || possibleMove.score > bestGame.score)) {
                    bestGame = possibleMove;
                }
            } else {
                MovePossibility finalBestGame = bestGame;
                getAllPossibleMoves(possibleMove.boardInstance).stream()
                        .map(move -> new MovePossibility(possibleMove, move))
                        .filter(move -> finalBestGame == null
                                || move.maxPossibleScore + move.score > finalBestGame.score)
                        .forEach(possibleMoves::add);
            }
        }

        return extractMoves(bestGame);
    }
}
