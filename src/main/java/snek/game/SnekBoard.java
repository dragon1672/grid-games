package snek.game;

import common.board.ReadOnlyBoard;
import common.utils.Flogger;
import common.utils.IntVector2;

public class SnekBoard implements ReadOnlyBoard<SnekCell> {
    private static final Flogger logger = Flogger.getInstance();
    private final int width;
    private final int height;
    private IntVector2 applePos;
    private SnekBody sneckBody;

    public SnekBoard(int width, int height, IntVector2 applePos, SnekBody sneckBody) {
        this.width = width;
        this.height = height;
        this.applePos = applePos;
        this.sneckBody = sneckBody;
    }

    public IntVector2 getApplePos() {
        return applePos;
    }

    public void setApplePos(IntVector2 applePos) {
        this.applePos = applePos;
    }

    @Override
    public SnekCell get(int x, int y) {
        return get(IntVector2.of(x, y));
    }

    @Override
    public SnekCell get(IntVector2 pos) {
        if (sneckBody.getHead().equals(pos)) {
            return SnekCell.HEAD;
        }
        if (sneckBody.getBodyPositions().contains(pos)) {
            return SnekCell.BODY;
        }
        if (applePos.equals(pos)) {
            return SnekCell.APPLE;
        }
        return SnekCell.EMPTY;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
