package popit;

import common.board.ReadOnlyBoard;
import common.gui.BoardGui;
import common.utils.AsciiBoard;
import common.utils.Flogger;
import common.utils.IntVector2;
import popit.ai.FirstPathNonRecursive;
import popit.ai.PopItAi;
import popit.game.BlockColor;
import popit.game.PopItGame;

import java.awt.*;
import java.util.List;

/**
 * Online Example: https://www.chiark.greenend.org.uk/~sgtatham/puzzles/js/samegame.html
 */
public class PopItDriver {
    private static final Flogger logger = Flogger.getInstance();

    private static Color cell2Color(BlockColor blockColor) {
        switch (blockColor) {
            case RED:
                return Color.RED;
            case GREEN:
                return Color.GREEN;
            case BROWN:
                return Color.GRAY;
            case PURPLE:
                return Color.MAGENTA;
            case INDIGO:
                return Color.BLUE;
            case YELLOW:
                return Color.YELLOW;
            case WHITES_INVALID:
                return Color.WHITE;
        }
        throw new IllegalArgumentException("wat");
    }

    private static String getStaticBoardString() {
        /*
        return "" +
                "    Y    \n" +
                "    Y    \n" +
                "    PI   \n" +
                "    PB   \n" +
                "I B IGI  \n" +
                "IIB BGB Y\n" +
                "PBIYIYG P\n" +
                "PPYYYYYGI" +
                "";
        /*/
        return "" +
                "GYYRGRGGGY\n" +
                "RRGRGYGGYR\n" +
                "GRRRGRYYYG\n" +
                "RGRGGGGRGY\n" +
                "GGRYYRRYGG\n" +
                "GGGYYGGGGY\n" +
                "RRYRYRRGGG\n" +
                "RRGGYYRYRG\n" +
                "RYYYRGRRGR\n" +
                "RGYGRYGGGY" +
                "";
        //*/
    }

    private static void runAi(PopItAi ai, PopItGame game, BoardGui<BlockColor> gui) throws InterruptedException {
        logger.atInfo().log("Staring PopIt Game");

        List<IntVector2> moves = ai.getMoves(game.getBoard());
        for (IntVector2 move : moves) {
            int newPoints = game.popAt(move);
            logger.atInfo().log("move: %s, new points: %s, current score: %s\nboard\n%s", move, newPoints, game.getScore(), AsciiBoard.boardToString(game.getBoard()));
            gui.updateBoard(game.getBoard());
            Thread.sleep(1000);
        }

        if (game.isComplete()) {
            logger.atInfo().log("AI completed board successfully with a score of %s", game.getScore());
        } else {
            logger.atInfo().log("AI failed");
        }
    }

    public static void main(String... args) throws InterruptedException {

        logger.atInfo().log("Staring game");

        // Create Game board
        ReadOnlyBoard<BlockColor> initialBoard;
        //initialBoard = BoardLoaders.generateRandomBoard(10, 10);
        initialBoard = BoardLoaders.generateFromString(getStaticBoardString());

        BoardGui<BlockColor> gui = BoardGui.createColorBoard(PopItDriver::cell2Color);

        PopItGame game = new PopItGame(initialBoard);

        // Set an MineSweeperAI
        PopItAi ai;
        ai = new FirstPathNonRecursive(gui);
        //ai = new RecursiveBoardHeuristic(gui);
        //ai = new RecursiveFirstPath(gui);
        //ai = new SmartestAI(gui);

        // Run Simulation
        runAi(ai, game, gui);

        logger.atInfo().log("Complete");

    }
}
