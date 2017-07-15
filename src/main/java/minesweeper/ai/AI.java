package minesweeper.ai;

import common.board.ReadOnlyBoard;
import common.utils.IntVector2;
import minesweeper.game.Cell;

public interface AI {
    IntVector2 getMove(ReadOnlyBoard<Cell> board);
}
