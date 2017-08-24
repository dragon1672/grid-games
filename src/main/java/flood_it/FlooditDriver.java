package flood_it;

import common.gui.BoardGui;
import common.interfaces.Runner;
import common.utils.AsciiBoard;
import common.utils.Flogger;
import flood_it.ai.FlooditAI;
import flood_it.ai.MaxAreaFlooditAi;
import flood_it.game.FloodColor;
import flood_it.game.FloodIt;

import java.awt.*;

/**
 * Runner Class
 */
public class FlooditDriver implements Runner<FloodColor> {

    private static final Flogger logger = Flogger.getInstance();

    public static Color cell2Color(FloodColor floodColor) {
        switch (floodColor) {
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

    private static void runAi(FlooditAI ai, FloodIt game, BoardGui<FloodColor> gui) throws InterruptedException {
        logger.atInfo().log("Staring Floodit AI moves");

        int moves = 0;

        while (!game.isComplete()) {
            moves++;
            FloodColor move = ai.getMove(game.getBoard());
            logger.atInfo().log("Floodit AI picked color %s", move);
            game.floodColor(move);
            gui.updateBoard(game.getBoard());
            logger.atInfo().log("board state\n%s", AsciiBoard.boardToString(game.getBoard()));
            Thread.sleep(1000);
        }

        logger.atInfo().log("Game Complete after %d rounds", moves);
    }

    @Override
    public BoardGui<FloodColor> getGui() {
        return BoardGui.createColorBoard(FlooditDriver::cell2Color);
    }

    @Override
    public void run(BoardGui<FloodColor> gui) throws InterruptedException {
        logger.atInfo().log("Staring game");

        // Create Game board
        FloodIt game = FloodIt.startGame(10, 10);

        // Set an TribbleAI
        FlooditAI ai;
        //ai = new RandomFlooditAi();
        //ai = new MaxPerimeterFlooditAi();
        ai = new MaxAreaFlooditAi();

        // Run Simulation
        runAi(ai, game, gui);

        logger.atInfo().log("Complete");
    }

    public static void main(String... args) throws InterruptedException {
        FlooditDriver driver = new FlooditDriver();
        driver.run(driver.getGui());
    }
}
