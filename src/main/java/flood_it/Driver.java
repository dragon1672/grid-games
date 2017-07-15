package flood_it;

import com.google.common.collect.ImmutableList;
import common.gui.BoardGui;
import common.utils.AsciiBoard;
import common.utils.Flogger;
import flood_it.ai.AI;
import flood_it.ai.MaxAreaAi;
import flood_it.ai.MaxPerimeterAi;
import flood_it.ai.RandomAi;
import flood_it.game.BlockColor;
import flood_it.game.FloodIt;

import java.awt.*;
import java.util.List;

/**
 * Runner Class
 */
public class Driver {

    private static final Flogger logger = Flogger.getInstance();

    @SuppressWarnings("unused")
    private static final List<AI> AIs = ImmutableList.of(new RandomAi(), new MaxPerimeterAi(), new MaxAreaAi());

    private static Color cell2Color(BlockColor blockColor) {
        switch (blockColor) {
            case RED:
                return Color.RED;
            case YELLOW:
                return Color.YELLOW;
            case GREEN:
                return Color.GREEN;
            case LIGHT_BLUE:
                return Color.CYAN;
            case BLUE:
                return Color.BLUE;
            case PURPLE:
                return Color.MAGENTA;
        }
        throw new IllegalArgumentException("wat");
    }

    private static void runAi(AI ai, FloodIt game) throws InterruptedException {
        BoardGui<BlockColor> gui = BoardGui.createColorBoard(Driver::cell2Color);
        logger.atInfo().log("Staring AI moves");

        int moves = 0;

        while (!game.isComplete()) {
            moves++;
            BlockColor move = ai.getMove(game);
            logger.atInfo().log("AI picked color %s", move);
            game.floodColor(move);
            gui.updateBoard(game.getBoard());
            logger.atInfo().log("board state\n%s", AsciiBoard.boardToString(game.getBoard()));
            Thread.sleep(1000);
        }

        logger.atInfo().log("Game Complete after %d rounds", moves);
    }

    public static void main(String... args) throws InterruptedException {

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
