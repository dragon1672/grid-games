package popit.ai;

import common.board.ReadOnlyBoard;
import common.gui.BoardGui;
import common.utils.BoardUtils;
import common.utils.IntVector2;
import popit.game.BlockColor;
import popit.game.PopItBoardUtilities;
import popit.game.PopItGame;

import java.util.*;

/**
 * Explores all possible outcomes and returns the best solutions
 * Right now way to slow to be useable
 */
@SuppressWarnings("unused")
public class SmartNoRecursive extends AI {
    private final BoardGui<BlockColor> gui;

    public SmartNoRecursive(BoardGui<BlockColor> gui) {
        this.gui = gui;
    }

    private static class MovePossibility {
        final MovePossibility parent;
        final ReadOnlyBoard<BlockColor> boardInstance;
        final IntVector2 moveToMake;
        final int score;

        MovePossibility(ReadOnlyBoard<BlockColor> boardInstance, IntVector2 moveToMake) {
            this.parent = null;
            this.moveToMake = moveToMake;
            Set<IntVector2> cellsToRemove = BoardUtils.getConnectedCells(boardInstance, moveToMake);
            this.boardInstance = PopItBoardUtilities.removeCells(boardInstance, cellsToRemove);
            this.score = PopItGame.calculateScore(cellsToRemove.size());
        }

        MovePossibility(MovePossibility parent, IntVector2 moveToMake) {
            this.parent = parent;
            this.moveToMake = moveToMake;
            Set<IntVector2> cellsToRemove = BoardUtils.getConnectedCells(parent.boardInstance, moveToMake);
            this.boardInstance = PopItBoardUtilities.removeCells(parent.boardInstance, cellsToRemove);
            this.score = parent.score + PopItGame.calculateScore(cellsToRemove.size());
        }

        boolean isComplete() {
            return PopItBoardUtilities.isEmpty(this.boardInstance);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MovePossibility that = (MovePossibility) o;

            if (score != that.score) return false;
            if (boardInstance != null ? !boardInstance.equals(that.boardInstance) : that.boardInstance != null)
                return false;
            return moveToMake != null ? moveToMake.equals(that.moveToMake) : that.moveToMake == null;
        }

        @Override
        public int hashCode() {
            int result = boardInstance != null ? boardInstance.hashCode() : 0;
            result = 31 * result + (moveToMake != null ? moveToMake.hashCode() : 0);
            result = 31 * result + score;
            return result;
        }
    }

    private MovePossibility convertToPossibleMove(ReadOnlyBoard<BlockColor> board, IntVector2 move) {
        return new MovePossibility(board, move);
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

        Set<MovePossibility> completedMoves = new HashSet<>();

        int currentMaxScore = 0;

        while (!possibleMoves.isEmpty()) {
            MovePossibility possibleMove = possibleMoves.pop();
            gui.updateBoard(possibleMove.boardInstance);

            if (possibleMove.isComplete() || AI.unWinnable(possibleMove.boardInstance)) {
                if (possibleMove.isComplete() && possibleMove.score > currentMaxScore) {
                    currentMaxScore = Math.max(currentMaxScore, possibleMove.score);
                    completedMoves.add(possibleMove);
                }
            } else {
                getAllPossibleMoves(possibleMove.boardInstance).stream()
                        .map(move -> new MovePossibility(possibleMove, move))
                        .forEach(possibleMoves::add);
            }
        }

        MovePossibility bestGame = completedMoves.stream().max(Comparator.comparingInt(move -> move.score)).orElseThrow(() -> new IllegalArgumentException("Un winnable game"));

        List<IntVector2> moves = extractMoves(bestGame);

        return moves;
    }
}
