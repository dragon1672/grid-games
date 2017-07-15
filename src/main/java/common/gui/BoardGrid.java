package common.gui;

import common.board.ReadOnlyBoard;

import javax.swing.*;

/**
 * Used for displaying a board
 */
abstract class BoardGrid<T> extends JPanel {
    abstract void updateBoard(ReadOnlyBoard<T> boardToUpdate);
}
