package flood_it.ai;

import common.board.ReadOnlyBoard;
import flood_it.game.FloodColor;
import flood_it.game.FloodItBoardUtilities;

import java.util.List;
import java.util.Random;

/**
 * Randomly Fills junk
 */
public class RandomFlooditAi implements FlooditAI {
    private static final Random rand = new Random();

    @Override
    public FloodColor getMove(ReadOnlyBoard<FloodColor> board) {
        List<FloodColor> possibleMoves = FloodItBoardUtilities.movesOnBoard(board);

        return possibleMoves.get(rand.nextInt(possibleMoves.size()));
    }
}
