package flood_it;

import com.google.common.collect.ImmutableList;
import common.gui.BoardGui;
import common.utils.AsciiBoard;
import common.utils.Flogger;
import flood_it.ai.FlooditAI;
import flood_it.ai.MaxAreaFlooditAi;
import flood_it.ai.MaxPerimeterFlooditAi;
import flood_it.ai.RandomFlooditAi;
import flood_it.game.BlockColor;
import flood_it.game.FloodIt;

import java.awt.*;
import java.util.List;

/**
 * Runner Class
 */
public class FlooditDriver {

    private static final Flogger logger = Flogger.getInstance();

    @SuppressWarnings("unused")
    private static final List<FlooditAI> AIs = ImmutableList.of(new RandomFlooditAi(), new MaxPerimeterFlooditAi(), new MaxAreaFlooditAi());

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

    private static void runAi(FlooditAI ai, FloodIt game) throws InterruptedException {
        BoardGui<BlockColor> gui = BoardGui.createColorBoard(FlooditDriver::cell2Color);
        logger.atInfo().log("Staring MineSweeperAI moves");

        int moves = 0;

        while (!game.isComplete()) {
            moves++;
            BlockColor move = ai.getMove(game);
            logger.atInfo().log("MineSweeperAI picked color %s", move);
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

        // Set an MineSweeperAI
        FlooditAI ai;
        //ai = new RandomMineSweeperAi();
        //ai = new MaxPerimeterFlooditAi();
        ai = new MaxAreaFlooditAi();

        // Run Simulation
        runAi(ai, game);

        logger.atInfo().log("Complete");

    }
}
