package game2048;

import com.google.common.collect.ImmutableMap;
import common.gui.BoardGui;
import common.utils.AsciiBoard;
import common.utils.Flogger;
import game2048.ai.AI2048;
import game2048.ai.Random2048AI;
import game2048.game.Cell;
import game2048.game.Game2048;
import game2048.game.Move;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Online example: https://www.chiark.greenend.org.uk/~sgtatham/puzzles/js/mines.html
 */
public class Driver2048 {
    private static final Flogger logger = Flogger.getInstance();

    private static Image loadImage(String fileName) {
        try {
            String fullPath = String.format("src/main/java/game2048/assets/%s", fileName);
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
            .put(Cell.N6, loadImage("6.PNG"))
            .put(Cell.N7, loadImage("7.PNG"))
            .put(Cell.N8, loadImage("8.PNG"))
            .build();

    private static void runAi(AI2048 ai, Game2048 game) throws InterruptedException {
        BoardGui<Cell> gui = BoardGui.createImageBoard(cellMap::get);
        logger.atInfo().log("Staring 2048 moves");

        int moves = 0;

        while (!game.isComplete()) {
            moves++;
            Move move = ai.getMove(game.getBoard());
            logger.atInfo().log("2048 AI picked color %s", move);
            game.move(move);
            gui.updateBoard(game.getBoard());
            logger.atInfo().log("board state\n%s", AsciiBoard.boardToString(game.getBoard()));
            Thread.sleep(1000);
        }

        logger.atInfo().log("Game Complete after %d rounds", moves);

    }

    public static void main(String... args) throws InterruptedException {
        logger.atInfo().log("Staring game");

        // Create Game board
        Game2048 game = Game2048.create(5, 5);

        // Set an MineSweeperAI
        AI2048 ai;
        //ai = new RandomMineSweeperAi();
        ai = new Random2048AI();

        runAi(ai, game);

        logger.atInfo().log("Complete");
    }
}
