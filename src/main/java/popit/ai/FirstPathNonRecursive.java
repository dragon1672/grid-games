package popit.ai;

import common.board.ReadOnlyBoard;
import common.gui.BoardGui;
import common.utils.BoardUtils;
import common.utils.IntVector2;
import popit.game.BlockColor;
import popit.game.PopItBoardUtilities;
import popit.game.PopItGame;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Returns the first found path not using recursion
 */
public class FirstPathNonRecursive extends AI {
    private final BoardGui<BlockColor> gui;

    public FirstPathNonRecursive(BoardGui<BlockColor> gui) {
        this.gui = gui;
    }

    private static int boardHeuristicMovePoints(ReadOnlyBoard<BlockColor> board) {
        return getAllPossibleMovesMap(board).values().stream().reduce((integer, integer2) -> integer + integer2).orElse(-1);
    }

    private static class MovePossibility {
        final MovePossibility parent;
        final ReadOnlyBoard<BlockColor> boardInstance;
        final IntVector2 moveToMake;
        final long score;
        final int boardHeuristic;

        MovePossibility(ReadOnlyBoard<BlockColor> boardInstance, IntVector2 moveToMake) {
            this.parent = null;
            this.moveToMake = moveToMake;
            Set<IntVector2> cellsToRemove = BoardUtils.getConnectedCells(boardInstance, moveToMake);
            this.boardInstance = PopItBoardUtilities.removeCells(boardInstance, cellsToRemove);
            this.score = PopItGame.calculateScore(cellsToRemove.size());
            this.boardHeuristic = boardHeuristicMovePoints(this.boardInstance);
        }

        MovePossibility(MovePossibility parent, IntVector2 moveToMake) {
            this.parent = parent;
            this.moveToMake = moveToMake;
            Set<IntVector2> cellsToRemove = BoardUtils.getConnectedCells(parent.boardInstance, moveToMake);
            this.boardInstance = PopItBoardUtilities.removeCells(parent.boardInstance, cellsToRemove);
            this.score = PopItGame.calculateScore(cellsToRemove.size());
            this.boardHeuristic = boardHeuristicMovePoints(this.boardInstance);
        }

        boolean isComplete() {
            return PopItBoardUtilities.isEmpty(this.boardInstance);
        }

        @SuppressWarnings("SimplifiableIfStatement")
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MovePossibility that = (MovePossibility) o;

            if (score != that.score) return false;
            if (boardHeuristic != that.boardHeuristic) return false;
            if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
            if (!boardInstance.equals(that.boardInstance)) return false;
            return moveToMake.equals(that.moveToMake);
        }

        @Override
        public int hashCode() {
            int result = parent != null ? parent.hashCode() : 0;
            result = 31 * result + boardInstance.hashCode();
            result = 31 * result + moveToMake.hashCode();
            result = 31 * result + (int) (score ^ (score >>> 32));
            result = 31 * result + boardHeuristic;
            return result;
        }
    }

    private Stream<MovePossibility> getAllMoves(ReadOnlyBoard<BlockColor> boardInput) {
        return getAllPossibleMoves(boardInput)
                .stream()
                .map(move -> new MovePossibility(boardInput, move));
    }

    private Stream<MovePossibility> getAllMoves(MovePossibility parent) {
        return getAllPossibleMoves(parent.boardInstance)
                .stream()
                .map(move -> new MovePossibility(parent, move));
    }

    private List<IntVector2> makeMoveList(MovePossibility movePossibility) {
        if (movePossibility == null) {
            return new ArrayList<>();
        }
        List<IntVector2> ret = makeMoveList(movePossibility.parent);
        ret.add(movePossibility.moveToMake);
        return ret;
    }

    @Override
    public List<IntVector2> getMoves(ReadOnlyBoard<BlockColor> originalBoard) {
        // can I thread this somehow?
        SortedSet<MovePossibility> possibleMoves = new TreeSet<>(Comparator.comparingInt(o -> o.boardHeuristic));
        possibleMoves.addAll(getAllMoves(originalBoard).collect(Collectors.toList()));
        while (!possibleMoves.isEmpty()) {
            // calling .first() or .last() wasn't giving me the proper sorted item :(
            MovePossibility possibleMove = possibleMoves.stream().findFirst().orElseThrow(() -> new IllegalArgumentException("No possible moves"));
            gui.updateBoard(possibleMove.boardInstance);
            possibleMoves.remove(possibleMove);

            if (possibleMove.isComplete()) {
                return makeMoveList(possibleMove);
            }
            possibleMoves.addAll(getAllMoves(possibleMove).collect(Collectors.toList()));
        }
        throw new IllegalArgumentException("Board is unsolvable");
    }
}
