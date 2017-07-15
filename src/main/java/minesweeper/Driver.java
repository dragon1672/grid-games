package minesweeper;

import common.gui.BoardGui;
import common.utils.AsciiBoard;
import common.utils.Flogger;
import common.utils.IntVector2;
import minesweeper.ai.AI;
import minesweeper.ai.RandomAi;
import minesweeper.game.Cell;
import minesweeper.game.MineSweeper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Online example: https://www.chiark.greenend.org.uk/~sgtatham/puzzles/js/mines.html
 */
public class Driver {
    private static final Flogger logger = Flogger.getInstance();

    private static Image cellToImageFetcher(Cell cell) {
        try {
            switch (cell) {
                case N0:
                    return ImageIO.read(new File("D:/AnthonyThings/Repos/Java/game_collection/src/main/java/minesweeper/assets/0.PNG"));
                case N1:
                    return ImageIO.read(new File("D:/AnthonyThings/Repos/Java/game_collection/src/main/java/minesweeper/assets/1.PNG"));
                case N2:
                    return ImageIO.read(new File("D:/AnthonyThings/Repos/Java/game_collection/src/main/java/minesweeper/assets/2.PNG"));
                case N3:
                    return ImageIO.read(new File("D:/AnthonyThings/Repos/Java/game_collection/src/main/java/minesweeper/assets/3.PNG"));
                case N4:
                    return ImageIO.read(new File("D:/AnthonyThings/Repos/Java/game_collection/src/main/java/minesweeper/assets/4.PNG"));
                case N5:
                    return ImageIO.read(new File("D:/AnthonyThings/Repos/Java/game_collection/src/main/java/minesweeper/assets/5.PNG"));
                case N6:
                    return ImageIO.read(new File("D:/AnthonyThings/Repos/Java/game_collection/src/main/java/minesweeper/assets/6.PNG"));
                case N7:
                    return ImageIO.read(new File("D:/AnthonyThings/Repos/Java/game_collection/src/main/java/minesweeper/assets/7.PNG"));
                case N8:
                    return ImageIO.read(new File("D:/AnthonyThings/Repos/Java/game_collection/src/main/java/minesweeper/assets/8.PNG"));
                case BOMB:
                    return ImageIO.read(new File("D:/AnthonyThings/Repos/Java/game_collection/src/main/java/minesweeper/assets/BOMB.PNG"));
                case EMPTY:
                    return ImageIO.read(new File("D:/AnthonyThings/Repos/Java/game_collection/src/main/java/minesweeper/assets/EMPTY.PNG"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("wat");
    }

    private static final Map<Cell, Image> imageCache = new HashMap<>();

    private static Image cellToImageConverter(Cell cell) {
        return imageCache.computeIfAbsent(cell, Driver::cellToImageFetcher);
    }

    private static void runAi(AI ai, MineSweeper game) throws InterruptedException {
        BoardGui<Cell> gui = BoardGui.createImageBoard(Driver::cellToImageConverter);
        logger.atInfo().log("Staring AI moves");

        int moves = 0;

        while (!game.isComplete()) {
            moves++;
            IntVector2 move = ai.getMove(game.getBoard());
            logger.atInfo().log("AI picked color %s", move);
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
        }
    }

    public static void main(String... args) throws InterruptedException {
        logger.atInfo().log("Staring game");

        // Create Game board
        MineSweeper game = MineSweeper.create(10, 10, 9);

        // Set an AI
        AI ai;
        ai = new RandomAi();

        // Run Simulation
        runAi(ai, game);

        logger.atInfo().log("Complete");
    }
}
