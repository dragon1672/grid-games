package flood_it.ai;

import flood_it.game.BlockColor;
import flood_it.game.FloodIt;

public interface FlooditAI {
    BlockColor getMove(FloodIt game);
}
