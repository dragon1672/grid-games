package popit.ai;

import common.board.ReadOnlyBoard;
import common.gui.BoardGui;
import common.utils.BoardUtils;
import common.utils.IntVector2;
import common.utils.Tuple;
import common.utils.Tuple.Tuple3;
import popit.game.BlockColor;
import popit.game.PopItBoardUtilities;

import java.util.*;

/**
 * Sorts proposed moved based off improved board heuristic
 */
public class RecursiveBoardHeuristic extends AI {
    private final BoardGui<BlockColor> gui;

    public RecursiveBoardHeuristic(BoardGui<BlockColor> gui) {
        this.gui = gui;
    }

    private static int boardHeuristicMovePoints(ReadOnlyBoard<BlockColor> board) {
        return getAllPossibleMovesMap(board).values().stream().reduce((integer, integer2) -> integer + integer2).orElse(-1);
    }

    /**
     * Apples move and display heuristic difference.
     *
     * @param board input board
     * @param move  move to take
     * @return Tuple of new board post move, the move, and the heuristic delta
     */
    private static Tuple3<ReadOnlyBoard<BlockColor>, IntVector2, Integer> boardHeuristicChange(ReadOnlyBoard<BlockColor> board, IntVector2 move) {
        int preMoveScore = boardHeuristicMovePoints(board);

        Set<IntVector2> cellsToRemove = BoardUtils.getConnectedCells(board, move);
        ReadOnlyBoard<BlockColor> boardInstance = PopItBoardUtilities.removeCells(board, cellsToRemove);

        int postMoveScore = boardHeuristicMovePoints(board);
        int heuristicDelta = postMoveScore - preMoveScore;

        return Tuple.of(boardInstance, move, heuristicDelta);
    }

    @Override
    public List<IntVector2> getMoves(ReadOnlyBoard<BlockColor> board) {
        Stack<IntVector2> winningMoveSet = new Stack<>();
        if (getWinningMoveSet(board, winningMoveSet)) {
            return winningMoveSet;
        }
        throw new IllegalArgumentException("provided board is unsolvable");
    }

    private boolean getWinningMoveSet(ReadOnlyBoard<BlockColor> boardToUse, Stack<IntVector2> moveList) {
        if (PopItBoardUtilities.isEmpty(boardToUse)) {
            return true;
        }
        Map<IntVector2, Integer> possibleMoves = getAllPossibleMovesMap(boardToUse);
        if (possibleMoves.isEmpty() || AI.unWinnable(boardToUse)) {
            return false;
        }
        return possibleMoves.keySet().stream()
                // dup board, preform move, save heuristic
                .map(move -> boardHeuristicChange(boardToUse, move))
                // sort by board heuristic
                .sorted(Comparator.comparingInt(Tuple3::getThird))
                // recurse
                .map(tuple -> {
                    ReadOnlyBoard<BlockColor> board = tuple.getFirst();
                    gui.updateBoard(board);
                    IntVector2 move = tuple.getSecond();
                    if (getWinningMoveSet(board, moveList)) {
                        moveList.insertElementAt(move, 0);
                        return true;
                    }
                    return false;
                })
                .filter(ret -> ret)
                .findFirst().orElse(false);
    }
}
