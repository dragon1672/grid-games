package popit;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import common.board.Board;
import common.board.BoardLoaders;
import common.board.ReadOnlyBoard;
import common.gui.BoardGui;
import common.interfaces.Runner;
import common.utils.AsciiBoard;
import common.utils.Flogger;
import common.utils.IntVector2;
import common.utils.RandomUtils;
import popit.ai.PopItAi;
import popit.ai.exhaustiveAIs.ExhaustiveIterativeAI;
import popit.game.BlockColor;
import popit.game.PopItGame;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Online Example: https://www.chiark.greenend.org.uk/~sgtatham/puzzles/js/samegame.html
 */
public class PopItDriver implements Runner<BlockColor> {
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

    private static final ImmutableBiMap<Character, BlockColor> char2Block = ImmutableBiMap.<Character, BlockColor>builder()
            .put('R', BlockColor.RED)
            .put('G', BlockColor.GREEN)
            .put('Y', BlockColor.YELLOW)
            .put('B', BlockColor.BROWN)
            .put('P', BlockColor.PURPLE)
            .put('I', BlockColor.INDIGO)
            .put(' ', BlockColor.WHITES_INVALID)
            .build();

    private static Board<BlockColor> getStaticBoard() {
        String boardStr = "" +
        /*
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
        return BoardLoaders.generateFromString(boardStr, ch -> char2Block.getOrDefault(ch, BlockColor.WHITES_INVALID));
    }

    private ReadOnlyBoard<BlockColor> getRandomBoard() {
        List<String> possibleSquares = ImmutableList.of(
                "" + BlockColor.RED.toString().charAt(0),
                "" + BlockColor.GREEN.toString().charAt(0),
                "" + BlockColor.BROWN.toString().charAt(0),
                "" + BlockColor.PURPLE.toString().charAt(0),
                "" + BlockColor.INDIGO.toString().charAt(0),
                "" + BlockColor.YELLOW.toString().charAt(0)
        );

        int width = 5;
        int height = 5;

        String boardStr = IntStream.range(0, height).mapToObj(y ->
                IntStream.range(0, width).mapToObj(value -> RandomUtils.randomFromList(possibleSquares))
                        .collect(Collectors.joining()))
                .collect(Collectors.joining("\n"));
        return BoardLoaders.generateFromString(boardStr, ch -> char2Block.getOrDefault(ch, BlockColor.WHITES_INVALID));
    }

    private static void runAi(PopItAi ai, PopItGame game, BoardGui<BlockColor> gui) throws InterruptedException {
        logger.atInfo().log("Staring PopIt Game");

        long startTime = System.currentTimeMillis();
        List<IntVector2> moves = ai.getMoves(game.getBoard());
        long endTime = System.currentTimeMillis();
        logger.atInfo().log("Getting moves took %d millis", endTime - startTime);
        for (IntVector2 move : moves) {
            long newPoints = game.popAt(move);
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

    @Override
    public BoardGui<BlockColor> getGui() {
        return BoardGui.createColorBoard(PopItDriver::cell2Color);
    }

    @Override
    public void run(BoardGui<BlockColor> gui) throws InterruptedException {
        logger.atInfo().log("Staring game");

        // Create Game board
        ReadOnlyBoard<BlockColor> initialBoard;
        initialBoard = getStaticBoard();
        initialBoard = getRandomBoard();

        PopItGame game = new PopItGame(initialBoard);

        // Set an PopItAiAI
        PopItAi ai;
        //ai = new FirstPathNonRecursive(gui);
        //ai = new RecursiveBoardHeuristic(gui);
        //ai = new RecursiveFirstPath(gui);
        //ai = new ExhaustiveRecursiveAI(gui);
        ai = new ExhaustiveIterativeAI(gui);
        //ai = new ExhaustiveThreadedAI(gui);

        // Run Simulation
        runAi(ai, game, gui);

        logger.atInfo().log("Complete");
    }

    public static void main(String... args) throws InterruptedException {
        PopItDriver driver = new PopItDriver();
        driver.run(driver.getGui());
    }
}
