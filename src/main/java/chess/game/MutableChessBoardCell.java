package chess.game;

import javax.annotation.Nullable;

public class MutableChessBoardCell extends ChessBoardCell {
    private ChessBoardPiece piece;

    public MutableChessBoardCell(ChessBoardCell.CellColor cellColor, @Nullable ChessBoardPiece piece) {
        super(cellColor);
        this.piece = piece;
    }

    @Override
    public ChessBoardPiece getPiece() {
        return piece;
    }

    public void setPiece(@Nullable ChessBoardPiece piece) {
        this.piece = piece;
    }
}
