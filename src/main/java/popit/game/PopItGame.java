package popit.game;

import common.board.BoardImpl;
import common.board.ReadOnlyBoard;
import common.interfaces.Game;
import common.utils.BoardUtils;
import common.utils.IntVector2;

import java.util.Set;

public class PopItGame implements Game<BlockColor> {
    private int score;
    private ReadOnlyBoard<BlockColor> board;

    public PopItGame(ReadOnlyBoard<BlockColor> board) {
        this.board = BoardImpl.copyOf(board);
    }

    @Override
    public boolean isComplete() {
        return PopItBoardUtilities.isEmpty(this.board);
    }

    @Override
    public ReadOnlyBoard<BlockColor> getBoard() {
        return board;
    }

    public static int calculateScore(int numCellsPopped) {
        return numCellsPopped * numCellsPopped - numCellsPopped;
    }

    public int popAt(IntVector2 move) {
        Set<IntVector2> poppedPositions = BoardUtils.getConnectedCells(board, move);
        this.board = PopItBoardUtilities.removeCells(board, poppedPositions);

        int pointsGained = calculateScore(poppedPositions.size());
        score += pointsGained;

        return pointsGained;
    }

    public int getScore() {
        return score;
    }
}
