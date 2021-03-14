package common.gui;

import common.board.ReadOnlyBoard;
import common.utils.IntVector2;

import java.awt.*;
import java.util.function.Function;

/**
 * Displays the board using colored squares
 */
class ImageBoard<T> extends BoardGrid<T> {

    private ReadOnlyBoard<T> board;
    private final Function<T, Image> imageConverter;
    private final int cellPadding;

    ImageBoard(Function<T, Image> imageConverter, int cellPadding) {
        this.imageConverter = imageConverter;
        this.cellPadding = cellPadding;
        setMinimumSize(new Dimension(BoardGui.windowDimensions, BoardGui.windowDimensions));
        setVisible(true);
    }

    private float getCellDimensions() {
        return BoardGui.getCellDimensions(board, cellPadding, getSize());
    }

    private void paintBoardWithImages(Graphics g, ReadOnlyBoard<T> board) {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                float cellDims = getCellDimensions();
                IntVector2 pos = IntVector2.of(x, y);
                IntVector2 coord = BoardGui.getStartPos(pos, cellDims, cellPadding);
                g.drawImage(imageConverter.apply(board.get(pos)), coord.x, coord.y, (int) cellDims, (int) cellDims, Color.BLACK, this);
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
            paintBoardWithImages(g, board);
        }
    }
}
