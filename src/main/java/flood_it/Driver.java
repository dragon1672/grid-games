package flood_it;

import com.google.common.collect.ImmutableList;
import common.utils.AsciiBoard;
import common.utils.Flogger;
import flood_it.ai.AI;
import flood_it.ai.MaxAreaAi;
import flood_it.ai.MaxPerimeterAi;
import flood_it.ai.RandomAi;
import flood_it.game.BlockColor;
import flood_it.game.FloodIt;

import java.util.List;

/**
 * Runner Class
 */
public class Driver {

    private static final Flogger logger = Flogger.getInstance();

    @SuppressWarnings("unused")
    private static final List<AI> AIs = ImmutableList.of(new RandomAi(), new MaxPerimeterAi(), new MaxAreaAi());

    private static void runAi(AI ai, FloodIt game) {
        logger.atInfo().log("Staring AI moves");

        int moves = 0;

        while (!game.isComplete()) {
            moves++;
            BlockColor move = ai.getMove(game);
            logger.atInfo().log("AI picked color %s", move);
            game.floodColor(move);
            logger.atInfo().log("board state\n%s", AsciiBoard.boardToString(game.getBoard()));
        }

        logger.atInfo().log("Game Complete after %d rounds", moves);
    }

    public static void main(String... args) {

        logger.atInfo().log("Staring game");

        // Create Game board
        FloodIt game = FloodIt.startGame(10, 10);

        // Set an AI
        AI ai;
        //ai = new RandomAi();
        //ai = new MaxPerimeterAi();
        ai = new MaxAreaAi();

        // Run Simulation
        runAi(ai, game);

        logger.atInfo().log("Complete");

    }
}
