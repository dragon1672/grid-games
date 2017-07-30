package popit.ai;

import common.board.ReadOnlyBoard;
import common.gui.BoardGui;
import common.utils.BoardUtils;
import common.utils.IntVector2;
import popit.game.BlockColor;
import popit.game.PopItBoardUtilities;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Brute force it!
 * Try out each possible move ordered by points
 * (Press the largest group and undo if that didn't work until it works)
 */
public class RecursiveFirstPath extends AI {
    private final BoardGui<BlockColor> gui;

    public RecursiveFirstPath(BoardGui<BlockColor> gui) {
        this.gui = gui;
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
                .sorted((o1, o2) -> possibleMoves.get(o2) - possibleMoves.get(o1))
                .map(move -> {
                    Set<IntVector2> cellsToRemove = BoardUtils.getConnectedCells(boardToUse, move);
                    ReadOnlyBoard<BlockColor> boardInstance = PopItBoardUtilities.removeCells(boardToUse, cellsToRemove);
                    gui.updateBoard(boardInstance);
                    if (getWinningMoveSet(boardInstance, moveList)) {
                        moveList.insertElementAt(move, 0);
                        return true;
                    }
                    return false;
                })
                .filter(ret -> ret)
                .findFirst().orElse(false);
    }
}
