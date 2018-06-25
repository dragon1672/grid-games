package chess.game;

public abstract class ChessBoardCell {
    public enum CellColor { White, Black }

    private final CellColor cellColor;
    protected ChessBoardCell(CellColor cellColor) {
        this.cellColor = cellColor;
    }
    public CellColor getColor() { return this.cellColor; }
    public abstract ChessBoardPiece getPiece();
}
