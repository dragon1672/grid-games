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

    ColorBoard(Function<T, Color> colorConverter) {
        this.colorConverter = colorConverter;
        setMinimumSize(new Dimension(BoardGui.windowDimensions, BoardGui.windowDimensions));
        setVisible(true);
    }

    private int getCellDimensions() {
        return BoardGui.getCellDimensions(board);
    }

    private void paintBoardWithColors(Graphics g, ReadOnlyBoard<T> board) {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                IntVector2 pos = IntVector2.of(x, y);
                g.setColor(colorConverter.apply(board.get(pos)));
                IntVector2 coord = BoardGui.getStartPos(pos, getCellDimensions());
                g.fillRect(coord.x, coord.y, getCellDimensions(), getCellDimensions());
            }
        }
    }

    @Override
    public void updateBoard(ReadOnlyBoard<T> board) {
        this.board = board;
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintBoardWithColors(g, board);
    }
}
