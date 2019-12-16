package minesweeper;

import com.google.common.collect.ImmutableMap;
import common.gui.BoardGui;
import common.interfaces.Runner;
import common.utils.AsciiBoard;
import common.utils.Flogger;
import common.utils.IntVector2;
import minesweeper.ai.GraphyMineSweeperAi;
import minesweeper.ai.MineSweeperAI;
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
            Thread.sleep(1000);
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
        MineSweeper game = MineSweeper.create(10, 10, 20);

        // Set an MineSweeperAI
        MineSweeperAI ai;
        //ai = new RandomMineSweeperAi();
        //ai = new SafeBetAI();
        ai = new GraphyMineSweeperAi();

        runAi(ai, game, gui);

        logger.atInfo().log("Complete");
    }

    public static void main(String... args) throws InterruptedException {
        MineSweeperDriver driver = new MineSweeperDriver();
        driver.run(driver.getGui());
    }
}
