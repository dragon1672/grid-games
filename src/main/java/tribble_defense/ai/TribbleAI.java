package tribble_defense.ai;

import common.board.ReadOnlyBoard;
import common.utils.IntVector2;
import tribble_defense.game.Cell;

public interface TribbleAI {
    IntVector2 getMove(ReadOnlyBoard<Cell> board);
}
