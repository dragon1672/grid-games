package tribble_defense.ai;

import common.board.ReadOnlyBoard;
import common.utils.BoardUtils;
import common.utils.IntVector2;
import tribble_defense.game.Cell;

public class RandomTribbleAi implements TribbleAI {

    @Override
    public IntVector2 getMove(ReadOnlyBoard<Cell> board) {
        return BoardUtils.boardPositionsAsRandomStream(board).filter(pos -> board.get(pos) == Cell.N0).findFirst().orElseThrow(() -> new IllegalArgumentException("No possible moves"));
    }
}
