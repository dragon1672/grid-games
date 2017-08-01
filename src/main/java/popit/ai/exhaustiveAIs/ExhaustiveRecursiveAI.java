package popit.ai.exhaustiveAIs;

import com.sun.istack.internal.Nullable;
import common.board.ReadOnlyBoard;
import common.gui.BoardGui;
import common.utils.BoardUtils;
import common.utils.IntVector2;
import popit.ai.AI;
import popit.game.BlockColor;
import popit.game.PopItBoardUtilities;
import popit.game.PopItGame;

import java.util.*;

/**
 * Explores all possible outcomes and returns the best solutions
 * Right now way to slow to be usable
 */
@SuppressWarnings("unused")
public class ExhaustiveRecursiveAI extends AI {
    private final BoardGui<BlockColor> gui;

    public ExhaustiveRecursiveAI(BoardGui<BlockColor> gui) {
        this.gui = gui;
    }

    @Override
    public List<IntVector2> getMoves(ReadOnlyBoard<BlockColor> board) {
        List<IntVector2> winningMoveSet = new ArrayList<>();
        getMaxMoveScore(board, winningMoveSet);
        Collections.reverse(winningMoveSet);
        return winningMoveSet;
    }

    private void getMaxMoveScore(ReadOnlyBoard<BlockColor> boardToUse, List<IntVector2> outputMoveList) {
        getMaxMoveScore(boardToUse, null, outputMoveList);
    }

    private long getMaxMoveScore(ReadOnlyBoard<BlockColor> board, @Nullable IntVector2 pos, List<IntVector2> outputMoveList) {
        long moveScore = 0;
        ReadOnlyBoard<BlockColor> boardToUse;
        if (pos != null) {

            Set<IntVector2> cellsToRemove = BoardUtils.getConnectedCells(board, pos);
            boardToUse = PopItBoardUtilities.removeCells(board, cellsToRemove);

            moveScore = PopItGame.calculateScore(cellsToRemove.size());

            if (BoardUtils.isUniformColor(boardToUse) || AI.unWinnable(boardToUse)) {
                return moveScore;
            }
        } else {
            boardToUse = board;
        }
        Set<IntVector2> possibleMoves = getAllPossibleMoves(boardToUse);
        if (possibleMoves.isEmpty()) {
            return -1;
        }
        Map<Long, List<IntVector2>> scoreToMoves = new HashMap<>();
        final long[] currentMax = {0};
        possibleMoves.parallelStream().forEach(move -> {
            List<IntVector2> moves = new ArrayList<>();
            gui.updateBoard(boardToUse);
            long moveMaxScore = getMaxMoveScore(boardToUse, move, moves);
            if (moveMaxScore > currentMax[0]) {
                synchronized (scoreToMoves) {
                    currentMax[0] = Math.max(currentMax[0], moveMaxScore);
                    scoreToMoves.put(moveMaxScore, moves);
                }
            }
        });


        Optional<Long> maxScore = scoreToMoves.keySet().stream().max(Long::compareTo);
        long finalMoveScore = moveScore;
        maxScore.map(returnScore -> returnScore + finalMoveScore);
        maxScore.ifPresent(score -> outputMoveList.addAll(scoreToMoves.get(score)));
        return maxScore.orElse(-1L);
    }
}
