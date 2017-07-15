package flood_it.ai;

import flood_it.game.BlockColor;
import flood_it.game.BoardUtilities;
import flood_it.game.FloodIt;

import java.util.List;
import java.util.Random;

/**
 * Randomly Fills junk
 */
public class RandomAi implements AI {
    private static final Random rand = new Random();

    @Override
    public BlockColor getMove(FloodIt game) {
        List<BlockColor> possibleMoves = BoardUtilities.movesOnBoard(game.getBoard());

        return possibleMoves.get(rand.nextInt(possibleMoves.size()));
    }
}
