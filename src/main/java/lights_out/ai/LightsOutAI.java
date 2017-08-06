package lights_out.ai;

import common.board.ReadOnlyBoard;
import common.utils.IntVector2;
import lights_out.game.LightsOutCell;

import java.util.Set;

public interface LightsOutAI {
    Set<IntVector2> getMoves(ReadOnlyBoard<LightsOutCell> board);
}
