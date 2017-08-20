package game2048.ai;

import common.board.ReadOnlyBoard;
import game2048.game.Cell;
import game2048.game.Move;

public interface AI2048 {
    Move getMove(ReadOnlyBoard<Cell> board);
}
