package lights_out;

import common.gui.BoardGui;
import common.interfaces.Runner;
import common.utils.AsciiBoard;
import common.utils.Flogger;
import common.utils.IntVector2;
import lights_out.ai.BillyTheBruteForceLightsOutAI;
import lights_out.ai.LightsOutAI;
import lights_out.game.LightsOutCell;
import lights_out.game.LightsOutGame;

import java.awt.*;
import java.util.Set;

/**
 * Online example: https://www.chiark.greenend.org.uk/~sgtatham/puzzles/js/flood.html
 */
public class LightsOutDriver implements Runner<LightsOutCell> {
    private static final Flogger logger = Flogger.getInstance();

    public static Color cell2Color(LightsOutCell lightsOutCell) {
        switch (lightsOutCell) {
            case TRUE:
                return Color.YELLOW;
            case FALSE:
                return Color.BLACK;
        }
        throw new IllegalArgumentException("wat");
    }

    private static void runAi(BoardGui<LightsOutCell> gui, LightsOutAI ai, LightsOutGame game) throws InterruptedException {
        logger.atInfo().log("Staring LightsOutAI moves");

        long startTime = System.currentTimeMillis();
        Set<IntVector2> moves = ai.getMoves(game.getBoard());
        long endTime = System.currentTimeMillis();

        logger.atInfo().log("Getting moves took %d milliseconds", endTime - startTime);

        for (IntVector2 move : moves) {
            logger.atInfo().log("Moving at %s", move);
            game.makeMove(move);
            gui.updateBoard(game.getBoard());
            logger.atInfo().log("board state\n%s", AsciiBoard.boardToString(game.getBoard()));
            Thread.sleep(1000);
        }

        logger.atInfo().log("Game Complete");
    }

    @Override
    public BoardGui<LightsOutCell> getGui() {
        return BoardGui.createColorBoard(LightsOutDriver::cell2Color);
    }

    @Override
    public void run(BoardGui<LightsOutCell> gui) throws InterruptedException {
        logger.atInfo().log("Staring game");

        // Create Game board
        LightsOutGame game = LightsOutGame.createGame(5, 5, true);

        LightsOutAI ai;
        ai = new BillyTheBruteForceLightsOutAI(gui);

        // Run Simulation
        runAi(gui, ai, game);

        logger.atInfo().log("Complete");
    }

    public static void main(String... args) throws InterruptedException {
        LightsOutDriver driver = new LightsOutDriver();
        driver.run(driver.getGui());
    }
}
