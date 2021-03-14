package common.gui;

import common.board.ReadOnlyBoard;
import common.utils.IntVector2;

import javax.swing.*;

/**
 * Used for displaying a board
 */
abstract class BoardGrid<T> extends JPanel {
    abstract void updateBoard(ReadOnlyBoard<T> boardToUpdate);

    abstract IntVector2 mouse2Board(IntVector2 mousePos);

}
