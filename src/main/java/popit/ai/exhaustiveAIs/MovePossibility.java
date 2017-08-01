package popit.ai.exhaustiveAIs;

import common.board.ReadOnlyBoard;
import common.utils.BoardUtils;
import common.utils.IntVector2;
import popit.ai.AI;
import popit.game.BlockColor;
import popit.game.PopItBoardUtilities;
import popit.game.PopItGame;

import java.util.Set;

public class MovePossibility {
    final MovePossibility parent;
    final ReadOnlyBoard<BlockColor> boardInstance;
    final IntVector2 moveToMake;
    final long score;
    final long maxPossibleScore;

    MovePossibility(ReadOnlyBoard<BlockColor> boardInstance, IntVector2 moveToMake) {
        this.parent = null;
        this.moveToMake = moveToMake;
        Set<IntVector2> cellsToRemove = BoardUtils.getConnectedCells(boardInstance, moveToMake);
        this.boardInstance = PopItBoardUtilities.removeCells(boardInstance, cellsToRemove);
        this.score = PopItGame.calculateScore(cellsToRemove.size());
        this.maxPossibleScore = AI.maxPossibleScore(this.boardInstance) + score;
    }

    MovePossibility(MovePossibility parent, IntVector2 moveToMake) {
        this.parent = parent;
        this.moveToMake = moveToMake;
        Set<IntVector2> cellsToRemove = BoardUtils.getConnectedCells(parent.boardInstance, moveToMake);
        this.boardInstance = PopItBoardUtilities.removeCells(parent.boardInstance, cellsToRemove);
        this.score = parent.score + PopItGame.calculateScore(cellsToRemove.size());
        this.maxPossibleScore = AI.maxPossibleScore(this.boardInstance) + score;
    }

    boolean isComplete() {
        return PopItBoardUtilities.isEmpty(this.boardInstance);
    }
}
