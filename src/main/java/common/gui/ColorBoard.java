package common.gui;

import common.board.ReadOnlyBoard;
import common.utils.IntVector2;

import java.awt.*;
import java.util.function.Function;

/**
 * Displays the board using colored squares
 */
class ColorBoard<T> extends BoardGrid<T> {

    private ReadOnlyBoard<T> board;
    private final Function<T, Color> colorConverter;
    private int cellPadding;

    ColorBoard(Function<T, Color> colorConverter, int cellPadding) {
        this.colorConverter = colorConverter;
        this.cellPadding = cellPadding;
        setMinimumSize(new Dimension(BoardGui.windowDimensions, BoardGui.windowDimensions));
        setVisible(true);
    }

    private float getCellDimensions() {
        return BoardGui.getCellDimensions(board, cellPadding, getSize());
    }

    private void paintBoardWithColors(Graphics g, ReadOnlyBoard<T> board) {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                float cellDims = getCellDimensions();
                IntVector2 pos = IntVector2.of(x, y);
                g.setColor(colorConverter.apply(board.get(pos)));
                IntVector2 coord = BoardGui.getStartPos(pos, cellDims, cellPadding);
                g.fillRect(coord.x, coord.y, (int) cellDims, (int) cellDims);
            }
        }
    }

    @Override
    public void updateBoard(ReadOnlyBoard<T> board) {
        this.board = board;
        this.repaint();
    }

    @Override
    public IntVector2 mouse2Board(IntVector2 mousePos) {
        return BoardGui.mouse2Board(mousePos, getCellDimensions(), cellPadding);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (board != null) {
            paintBoardWithColors(g, board);
        } else {
            String message = "Board Still Loading...";
            g.drawChars(message.toCharArray(), 0, message.length(), 100, 100);
        }
    }
}
