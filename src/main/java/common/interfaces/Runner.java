package common.interfaces;

import common.gui.BoardGui;

public interface Runner<T> {
    BoardGui<T> getGui();

    void run(BoardGui<T> gui) throws Exception;
}
