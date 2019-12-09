package conway_lyfe;

import com.google.common.collect.Iterables;
import common.gui.BoardGui;
import common.interfaces.Runner;
import common.utils.Flogger;
import common.utils.IntVector2;
import conway_lyfe.game.ConwayBoard;
import conway_lyfe.game.ConwayCell;
import conway_lyfe.game.ConwayGameBoard;

import java.awt.*;

public class ConwayDriver implements Runner<ConwayCell> {
    private static final Flogger logger = Flogger.getInstance();

    public static Color cell2Color(ConwayCell lightsOutCell) {
        switch (lightsOutCell) {
            case ALIVE:
                return Color.YELLOW;
            case DEAD:
                return Color.BLACK;
        }
        throw new IllegalArgumentException("wat");
    }

    @Override
    public BoardGui<ConwayCell> getGui() {
        return BoardGui.createColorBoard(ConwayDriver::cell2Color);
    }

    @Override
    public void run(BoardGui<ConwayCell> gui) throws Exception {
        logger.atInfo().log("Staring game");

        // Create Game board
        ConwayBoard board = ConwayBoard.builder()
                // Blinker
                .setActive(IntVector2.iVec(-2, -1))
                .setActive(IntVector2.iVec(-2, 0))
                .setActive(IntVector2.iVec(-2, 1))
                // Glider
                .setActive(IntVector2.iVec(0, -2))
                .setActive(IntVector2.iVec(2, -2))
                .setActive(IntVector2.iVec(1, -2))
                .setActive(IntVector2.iVec(2, -1))
                .setActive(IntVector2.iVec(1, 0))
                .build();

        // Run Simulation
        runAi(gui, board);

        logger.atInfo().log("Complete");
    }

    private void runAi(BoardGui<ConwayCell> gui, ConwayBoard firstBoard) throws InterruptedException {
        logger.atInfo().log("Starting simulation");

        ConwayBoard currentBoard = firstBoard;

        while (!Iterables.isEmpty(currentBoard.getPoints())) {
            gui.updateBoard(ConwayGameBoard.fromBoard(currentBoard));
            //Thread.sleep(500);
            Thread.sleep(200);
            currentBoard = currentBoard.evolve();
        }

        logger.atInfo().log("Game Complete");
    }

    public static void main(String... args) throws Exception {
        ConwayDriver driver = new ConwayDriver();
        driver.run(driver.getGui());
    }
}
