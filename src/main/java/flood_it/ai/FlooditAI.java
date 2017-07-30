package flood_it.ai;

import flood_it.game.FloodColor;
import flood_it.game.FloodIt;

public interface FlooditAI {
    FloodColor getMove(FloodIt game);
}
