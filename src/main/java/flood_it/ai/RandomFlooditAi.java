package flood_it.ai;

import flood_it.game.FloodColor;
import flood_it.game.FloodIt;
import flood_it.game.FloodItBoardUtilities;

import java.util.List;
import java.util.Random;

/**
 * Randomly Fills junk
 */
public class RandomFlooditAi implements FlooditAI {
    private static final Random rand = new Random();

    @Override
    public FloodColor getMove(FloodIt game) {
        List<FloodColor> possibleMoves = FloodItBoardUtilities.movesOnBoard(game.getBoard());

        return possibleMoves.get(rand.nextInt(possibleMoves.size()));
    }
}
