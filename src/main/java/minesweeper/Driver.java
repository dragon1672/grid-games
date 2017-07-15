package minesweeper;

import common.utils.AsciiBoard;
import common.utils.Flogger;
import common.utils.IntVector2;
import minesweeper.ai.AI;
import minesweeper.ai.RandomAi;
import minesweeper.game.MineSweeper;

public class Driver {
    private static final Flogger logger = Flogger.getInstance();

    private static void runAi(AI ai, MineSweeper game) {
        logger.atInfo().log("Staring AI moves");

        int moves = 0;

        while (!game.isComplete()) {
            moves++;
            IntVector2 move = ai.getMove(game.getBoard());
            logger.atInfo().log("AI picked color %s", move);
            game.move(move);
            logger.atInfo().log("board state\n%s", AsciiBoard.boardToString(game.getBoard()));
        }

        logger.atInfo().log("Game Complete after %d rounds", moves);

        if (game.hasWon()) {
            logger.atInfo().log("Congratulations you have won");
        }
        if (game.hasLost()) {
            logger.atInfo().log("You lost");
        }
    }

    public static void main(String... args) {
        logger.atInfo().log("Staring game");

        // Create Game board
        MineSweeper game = MineSweeper.create(10, 10, 5);

        // Set an AI
        AI ai;
        ai = new RandomAi();

        // Run Simulation
        runAi(ai, game);

        logger.atInfo().log("Complete");
    }
}
