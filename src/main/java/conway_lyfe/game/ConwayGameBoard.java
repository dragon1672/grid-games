package conway_lyfe.game;

import com.google.common.collect.Iterables;
import common.board.ReadOnlyBoard;
import common.utils.IntVector2;

public class ConwayGameBoard implements ReadOnlyBoard<ConwayCell> {
    private final ConwayReadOnlyBoard board;
    private IntVector2 lowBound;
    private IntVector2 highBound;

    public ConwayGameBoard(ConwayReadOnlyBoard board) {
        this.board = board;
    }

    private void calculateBounds() {
        IntVector2 firstPoint = Iterables.getFirst(board.getPoints(), null);
        if (firstPoint == null) {
            lowBound = IntVector2.of(0, 0);
            highBound = IntVector2.of(1, 1);
            return;
        }
        int lowX = firstPoint.x;
        int lowY = firstPoint.y;
        int highX = firstPoint.x;
        int highY = firstPoint.y;

        for (IntVector2 point : board.getPoints()) {
            lowX = Math.min(lowX, point.x);
            lowY = Math.min(lowY, point.y);
            highX = Math.max(highX, point.x);
            highY = Math.max(highY, point.y);
        }
        lowBound = IntVector2.of(lowX - 1, lowY - 1);
        highBound = IntVector2.of(highX + 1, highY + 1);
    }

    public static ConwayGameBoard fromBoard(ConwayBoard board) {
        ConwayGameBoard gameBoard = new ConwayGameBoard(board);
        gameBoard.calculateBounds();
        return gameBoard;
    }

    @Override
    public ConwayCell get(IntVector2 pos) {
        IntVector2 offsetPos = pos.add(lowBound);
        return board.isActive(offsetPos) ? ConwayCell.ALIVE : ConwayCell.DEAD;
    }

    @Override
    public ConwayCell get(int x, int y) {
        return get(IntVector2.iVec(x, y));
    }

    @Override
    public int getWidth() {
        return highBound.x - lowBound.x;
    }

    @Override
    public int getHeight() {
        return highBound.y - lowBound.y;
    }
}
