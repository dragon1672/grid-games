package common.gui;

import com.google.common.eventbus.EventBus;
import common.board.ReadOnlyBoard;
import common.utils.IntVector2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public class BoardGui<T> extends JFrame {

    private static final int outerPadding = 10;
    private static final int defaultCellPadding = 5;
    static final int windowDimensions = 1000;
    public final EventBus mouseClickEventBus = new EventBus();


    static <T> float getCellDimensions(ReadOnlyBoard<T> board, int cellPadding, Dimension dims) {
        float maxHeight = (float) (dims.height - outerPadding * 2) / (board.getHeight()) - cellPadding;
        float maxWidth = (float) (dims.width - outerPadding * 2) / (board.getWidth()) - cellPadding;
        return Math.min(maxHeight, maxWidth);
    }

    static IntVector2 getStartPos(IntVector2 coordinate, float cellDimensions, int cellPadding) {
        return coordinate.mul(cellDimensions + cellPadding).add(IntVector2.of(outerPadding, outerPadding));
    }

    static IntVector2 mouse2Board(IntVector2 mousePos, float cellDimensions, int cellPadding) {
        return mousePos.sub(IntVector2.of(outerPadding, outerPadding)).mul(1.0 / (cellDimensions + cellPadding));
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

        boardGrid.addMouseListener(new MouseAdapter() {// provides empty implementation of all
            // MouseListener`s methods, allowing us to
            // override only those which interests us
            @Override //I override only one method for presentation
            public void mousePressed(MouseEvent e) {
                IntVector2 mousePos = IntVector2.of(e.getX(), e.getY());
                IntVector2 boardPos = boardGrid.mouse2Board(mousePos);

                mouseClickEventBus.post(boardPos);
            }
        });
    }

    public static <T> BoardGui<T> createColorBoard(Function<T, Color> colorConverter) {
        return new BoardGui<>(new ColorBoard<>(colorConverter, defaultCellPadding));
    }

    public static <T> BoardGui<T> createColorBoard(Function<T, Color> colorConverter, int cellPadding) {
        return new BoardGui<>(new ColorBoard<>(colorConverter, cellPadding));
    }

    public static <T> BoardGui<T> createImageBoard(Function<T, Image> imageConverter) {
        return new BoardGui<>(new ImageBoard<>(imageConverter, defaultCellPadding));
    }

    public void updateBoard(ReadOnlyBoard<T> board) {
        boardGrid.updateBoard(board);
        repaint();
    }


}
