package popit.game;

import common.board.BoardImpl;
import common.board.ReadOnlyBoard;
import common.interfaces.Game;
import common.utils.BoardUtils;
import common.utils.IntVector2;

import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

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

    public static long calculateScore(long numCellsPopped) {
        return numCellsPopped * numCellsPopped - numCellsPopped;
    }

    public long popAt(IntVector2 move) {
        Set<IntVector2> poppedPositions = BoardUtils.getConnectedCells(board, move);

        checkArgument(poppedPositions.size() > 1, "Move must pop at least 2 cells");

        this.board = PopItBoardUtilities.removeCells(board, poppedPositions);

        long pointsGained = calculateScore(poppedPositions.size());
        score += pointsGained;

        return pointsGained;
    }

    public int getScore() {
        return score;
    }
}
