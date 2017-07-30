package common.gui;

import common.board.ReadOnlyBoard;
import common.utils.IntVector2;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

public class BoardGui<T> extends JFrame {

    private static final int outerPadding = 10;
    private static final int cellPadding = 10;
    static final int windowDimensions = 1000;

    static <T> int getCellDimensions(ReadOnlyBoard<T> board) {
        return (windowDimensions - outerPadding) / Math.max(board.getWidth() + 1, board.getHeight() + 1) - cellPadding;
    }

    static IntVector2 getStartPos(IntVector2 coordinate, int cellDimensions) {
        return coordinate.mul(cellDimensions + cellPadding).add(IntVector2.of(outerPadding, outerPadding));
    }


    private final BoardGrid<T> boardGrid;

    private BoardGui(BoardGrid<T> boardGrid) {
        this.boardGrid = boardGrid;
        setLayout(new BorderLayout());
        add(this.boardGrid, BorderLayout.CENTER);
        boardGrid.setLocation(0, 0);
        setMinimumSize(new Dimension(windowDimensions, windowDimensions));
        pack();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static <T> BoardGui<T> createColorBoard(Function<T, Color> colorConverter) {
        return new BoardGui<>(new ColorBoard<>(colorConverter));
    }

    public static <T> BoardGui<T> createImageBoard(Function<T, Image> imageConverter) {
        return new BoardGui<>(new ImageBoard<>(imageConverter));
    }

    public void updateBoard(ReadOnlyBoard<T> board) {
        boardGrid.updateBoard(board);
        repaint();
    }


}
