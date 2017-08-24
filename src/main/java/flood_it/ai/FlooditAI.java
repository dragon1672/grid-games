package flood_it.ai;

import common.board.ReadOnlyBoard;
import flood_it.game.FloodColor;

public interface FlooditAI {
    FloodColor getMove(ReadOnlyBoard<FloodColor> board);
}
