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

    ImageBoard(Function<T, Image> imageConverter) {
        this.imageConverter = imageConverter;
        setMinimumSize(new Dimension(BoardGui.windowDimensions, BoardGui.windowDimensions));
        setVisible(true);
    }

    private int getCellDimensions() {
        return BoardGui.getCellDimensions(board);
    }

    private void paintBoardWithImages(Graphics g, ReadOnlyBoard<T> board) {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                IntVector2 pos = IntVector2.of(x, y);
                IntVector2 coord = BoardGui.getStartPos(pos, getCellDimensions());
                g.drawImage(imageConverter.apply(board.get(pos)), coord.x, coord.y, getCellDimensions(), getCellDimensions(), Color.BLACK, this);
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
        paintBoardWithImages(g, board);
    }
}
