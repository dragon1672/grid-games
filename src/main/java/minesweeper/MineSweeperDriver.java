package minesweeper;

import com.google.common.collect.ImmutableMap;
import common.gui.BoardGui;
import common.interfaces.Runner;
import common.utils.AsciiBoard;
import common.utils.Flogger;
import common.utils.IntVector2;
import minesweeper.ai.MineSweeperAI;
import minesweeper.ai.SmaryPants;
import minesweeper.game.Cell;
import minesweeper.game.MineSweeper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Online example: https://www.chiark.greenend.org.uk/~sgtatham/puzzles/js/mines.html
 */
public class MineSweeperDriver implements Runner<Cell> {
    private static final Flogger logger = Flogger.getInstance();

    private static Image loadImage(String fileName) {
        try {
            String fullPath = String.format("src/main/java/minesweeper/assets/%s", fileName);
            return ImageIO.read(new File(fullPath));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // Map each cell to a file
    public static final ImmutableMap<Cell, Image> cellMap = ImmutableMap.<Cell, Image>builder()
            .put(Cell.N0, loadImage("0.PNG"))
            .put(Cell.N1, loadImage("1.PNG"))
            .put(Cell.N2, loadImage("2.PNG"))
            .put(Cell.N3, loadImage("3.PNG"))
            .put(Cell.N4, loadImage("4.PNG"))
            .put(Cell.N5, loadImage("5.PNG"))
            .put(Cell.N6, loadImage("6.PNG"))
            .put(Cell.N7, loadImage("7.PNG"))
            .put(Cell.N8, loadImage("8.PNG"))
            .put(Cell.BOMB, loadImage("BOMB.PNG"))
            .put(Cell.EMPTY, loadImage("EMPTY.PNG"))
            .build();

    private static void runAi(MineSweeperAI ai, MineSweeper game, BoardGui<Cell> gui) throws InterruptedException {
        logger.atInfo().log("Staring MineSweeperAI moves");

        int moves = 0;

        while (!game.isComplete()) {
            moves++;
            IntVector2 move = ai.getMove(game.getBoard(), game.getMineCount());
            logger.atInfo().log("MineSweeperAI picked color %s", move);
            game.move(move);
            gui.updateBoard(game.getBoard());
            logger.atInfo().log("board state\n%s", AsciiBoard.boardToString(game.getBoard()));
            Thread.sleep(50);
        }

        logger.atInfo().log("Game Complete after %d rounds", moves);

        if (game.hasWon()) {
            logger.atInfo().log("Congratulations you have won");
        }
        if (game.hasLost()) {
            logger.atInfo().log("You lost");
            game.revealAllBombs();
            gui.updateBoard(game.getBoard());
        }
    }

    @Override
    public BoardGui<Cell> getGui() {
        return BoardGui.createImageBoard(cellMap::get);
    }

    @Override
    public void run(BoardGui<Cell> gui) throws InterruptedException {
        logger.atInfo().log("Staring game");

        // Create Game board
        // Difficulties taken from
        // https://dash.harvard.edu/bitstream/handle/1/14398552/BECERRA-SENIORTHESIS-2015.pdf
        MineSweeper game =
                // Beginner has a total of ten mines and the board size is either 8 × 8, 9 × 9, or 10 × 10
                //MineSweeper.create(10, 10, 10); // 10% bombs
                // Intermediate has 40 mines and also varies in size between 13 × 15 and 16 × 16
                //MineSweeper.create(16, 16, 40); // 15.625% bombs
                // expert has 99 mines and is always 16 × 30 (or 30 × 16)
                //MineSweeper.create(16, 30, 99); // 20.625% bombs
                // Custom
                MineSweeper.create(100, 100, 2000); // 20% bombs

        // Set an MineSweeperAI
        MineSweeperAI ai;
        //ai = new RandomMineSweeperAi();
        //ai = new SafeBetAI();
        //ai = new GraphyMineSweeperAi();
        ai = new SmaryPants();

        SmaryPants.game = game;

        runAi(ai, game, gui);

        logger.atInfo().log("Complete");
    }

    public static void main(String... args) throws InterruptedException {
        MineSweeperDriver driver = new MineSweeperDriver();
        driver.run(driver.getGui());
    }
}
