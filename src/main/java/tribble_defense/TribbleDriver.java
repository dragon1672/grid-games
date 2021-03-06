package tribble_defense;

import com.google.common.collect.ImmutableMap;
import common.gui.BoardGui;
import common.interfaces.Runner;
import common.utils.AsciiBoard;
import common.utils.Flogger;
import common.utils.IntVector2;
import tribble_defense.ai.RandomTribbleAi;
import tribble_defense.ai.TribbleAI;
import tribble_defense.game.Cell;
import tribble_defense.game.TribbleDefense;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Online example: http://dragon1672.github.io/Tribble-Defense/source/index.html
 */
public class TribbleDriver implements Runner<Cell> {
    private static final Flogger logger = Flogger.getInstance();

    private static Image loadImage(String fileName) {
        try {
            String fullPath = String.format("src/main/java/tribble_defense/assets/%s", fileName);
            return ImageIO.read(new File(fullPath));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // Map each cell to a file
    private static final ImmutableMap<Cell, Image> cellMap = ImmutableMap.<Cell, Image>builder()
            .put(Cell.N0, loadImage("0.PNG"))
            .put(Cell.N1, loadImage("1.PNG"))
            .put(Cell.N2, loadImage("2.PNG"))
            .put(Cell.N3, loadImage("3.PNG"))
            .put(Cell.N4, loadImage("4.PNG"))
            .put(Cell.N5, loadImage("5.PNG"))
            .put(Cell.BLOCKED, loadImage("BLOCKER.png"))
            .build();

    private static void runAi(TribbleAI ai, TribbleDefense game, BoardGui<Cell> gui) throws InterruptedException {
        logger.atInfo().log("Staring TribbleAI moves");

        int moves = 0;

        while (!game.isComplete()) {
            moves++;
            IntVector2 move = ai.getMove(game.getBoard());
            logger.atInfo().log("TribbleAI picked color %s", move);
            game.move(move);
            gui.updateBoard(game.getBoard());
            logger.atInfo().log("board state\n%s", AsciiBoard.boardToString(game.getBoard()));
            Thread.sleep(1000);
        }

        logger.atInfo().log("Game Complete after %d rounds", moves);
    }

    @Override
    public BoardGui<Cell> getGui() {
        return BoardGui.createImageBoard(cellMap::get);
    }

    @Override
    public void run(BoardGui<Cell> gui) throws InterruptedException {
        logger.atInfo().log("Staring game");

        // Create Game board
        TribbleDefense game = TribbleDefense.create(5, 5, 5);

        // Set an TribbleAI
        TribbleAI ai;
        ai = new RandomTribbleAi();

        runAi(ai, game, gui);

        logger.atInfo().log("Complete");
    }

    public static void main(String... args) throws InterruptedException {
        TribbleDriver driver = new TribbleDriver();
        driver.run(driver.getGui());
    }
}
