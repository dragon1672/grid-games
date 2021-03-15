package nonogram.ai.multi_strat;

import com.google.common.collect.ImmutableMap;
import common.utils.IntVector2;
import nonogram.game.NonoGame;

public interface MultiStratAI {
    ImmutableMap<IntVector2, AIFlags> generateFlags(NonoGame game, ImmutableMap<IntVector2, AIFlags> existingFlags);
}
