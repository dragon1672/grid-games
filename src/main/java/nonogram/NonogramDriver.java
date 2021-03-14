package nonogram;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import common.gui.BoardClickListener;
import common.gui.BoardGui;
import common.interfaces.Runner;
import common.utils.Flogger;
import nonogram.ai.ExhaustiveFlatAI;
import nonogram.game.Cell;
import nonogram.game.FlatNonoGame;
import nonogram.game.NonoGame;
import nonogram.game.NonoGameFlatUnknowns;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;


public class NonogramDriver implements Runner<Cell> {
    private static final Flogger logger = Flogger.getInstance();


    private static Image loadImage(String fileName) {
        try {
            String fullPath = String.format("src/main/java/nonogram/assets/%s", fileName);
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
            .put(Cell.UNKNOWN, loadImage("BOMB.PNG"))
            .put(Cell.SELECTED, loadImage("SELECTED.PNG"))
            .build();

    @Override
    public BoardGui<Cell> getGui() {
        return BoardGui.createImageBoard(cellMap::get);
    }

    private void runFlats(BoardGui<Cell> gui) throws InterruptedException {
        logger.atInfo().log("Staring game");

        /* Static
        NonoGame game = new NonoGameKnownSolution(BoardLoaders.generateFromString("" +
                "......\n" +
                ".X..X.\n" +
                ".X..X.\n" +
                ".X..X.\n" +
                ".XXXX.\n" +
                "......\n" +
                "", c -> c.equals('X')));

        /*/ // unknowns

        FlatNonoGame game = new NonoGameFlatUnknowns(
                // Letter U
                //ImmutableList.of(4, 1, 1, 4, 0),
                //ImmutableList.of(0, 2, 2, 2, 4, 0)
                // Full Game
                //ImmutableList.of(5, 5, 2, 2, 2, 5, 3),
                //ImmutableList.of(3, 4, 3, 4, 4, 3, 3)
                // Big Game
                ImmutableList.of(37, 13, 24, 23, 17, 24, 28, 31, 23, 17, 17, 23, 20, 16, 15, 13, 18, 18, 18, 19, 19, 21, 16, 18, 20, 18, 18, 20, 21, 25, 24, 26, 21, 21, 34),
                ImmutableList.of(19, 14, 15, 13, 12, 14, 15, 15, 18, 18, 18, 17, 15, 16, 17, 27, 25, 9, 8, 17, 21, 28, 20, 21, 17, 18, 20, 22, 19, 19, 15, 18, 17, 16, 12, 11, 11, 11, 10, 11, 9, 12, 14, 12, 30)
        );

        //*/



        /* Human
        AtomicBoolean freeze = new AtomicBoolean(false);
        gui.mouseClickEventBus.register(new BoardClickListener(pos -> {
            if (freeze.get()) {
                logger.atInfo().log("Game is complete, input is frozen!");
                return;
            }
            logger.atInfo().log("Click @ %s", pos);
            try {
                game.toggle(pos);
            } catch (IllegalArgumentException e) {
            }// swallow
            gui.updateBoard(game.getBoard());
        }));

        while (!game.isComplete()) {
            // TODO do actual event handling or something
            Thread.sleep(100);
            gui.updateBoard(game.getBoard());
        }
        freeze.set(true);

        /*/ //  AI
        ExhaustiveFlatAI ai = new ExhaustiveFlatAI();

        ImmutableSet<FlatNonoGame> winners = ai.solve(game, gui);

        logger.atInfo().log("Displaying winners");
        for (FlatNonoGame winner : winners) {
            gui.updateBoard(winner.getBoard());
            Thread.sleep(1000);
        }

        //*/


        logger.atInfo().log("Complete");
    }

    private void runFullNono(BoardGui<Cell> gui) throws InterruptedException {
        logger.atInfo().log("Staring game");

        /* Big
        NonoGame game = new NonoGame(
                ImmutableList.of(
                        ImmutableList.of(7, 11, 4, 4, 11).reverse(),
                        ImmutableList.of(2, 2, 4, 1, 2, 2).reverse(),
                        ImmutableList.of(3, 2, 4, 7, 3, 2, 3).reverse(),
                        ImmutableList.of(5, 3, 4, 2, 1, 2, 2, 4).reverse(),
                        ImmutableList.of(6, 1, 2, 1, 2, 2, 2, 1).reverse(),
                        ImmutableList.of(1, 7, 1, 1, 3, 2, 1, 6, 2).reverse(),
                        ImmutableList.of(4, 2, 2, 2, 1, 3, 6, 4, 4).reverse(),
                        ImmutableList.of(9, 2, 3, 13, 3, 1).reverse(),
                        ImmutableList.of(1, 9, 1, 2, 4, 2, 3, 1).reverse(),
                        ImmutableList.of(2, 3, 1, 2, 1, 4, 3, 1).reverse(),
                        ImmutableList.of(2, 3, 2, 1, 5, 1, 2, 1).reverse(),
                        ImmutableList.of(2, 3, 7, 6, 1, 3, 1).reverse(),
                        ImmutableList.of(3, 2, 9, 1, 3, 1, 1).reverse(),
                        ImmutableList.of(6, 4, 5, 1).reverse(),
                        ImmutableList.of(6, 1, 2, 4, 1, 1).reverse(),
                        ImmutableList.of(2, 1, 2, 1, 1, 4, 1, 1).reverse(),
                        ImmutableList.of(4, 1, 2, 2, 6, 3).reverse(),
                        ImmutableList.of(1, 2, 2, 2, 2, 4, 5).reverse(),
                        ImmutableList.of(5, 2, 2, 4, 2, 3).reverse(),
                        ImmutableList.of(2, 2, 2, 1, 1, 4, 2, 2, 1).reverse(),
                        ImmutableList.of(1, 2, 3, 1, 2, 3, 2, 3, 1).reverse(),
                        ImmutableList.of(2, 3, 2, 1, 2, 7, 3, 1).reverse(),
                        ImmutableList.of(1, 3, 3, 2, 2, 3, 1, 1).reverse(),
                        ImmutableList.of(1, 4, 4, 2, 4, 2, 1).reverse(),
                        ImmutableList.of(2, 3, 4, 2, 8, 1).reverse(),
                        ImmutableList.of(2, 6, 5, 2, 2, 1).reverse(),
                        ImmutableList.of(3, 4, 2, 5, 3, 1).reverse(),
                        ImmutableList.of(3, 4, 3, 5, 2, 2, 1).reverse(),
                        ImmutableList.of(1, 1, 4, 3, 10, 2).reverse(),
                        ImmutableList.of(1, 1, 5, 4, 11, 1, 2).reverse(),
                        ImmutableList.of(1, 4, 5, 7, 3, 1, 3).reverse(),
                        ImmutableList.of(1, 3, 7, 4, 2, 9).reverse(),
                        ImmutableList.of(1, 2, 9, 4, 2, 2, 1).reverse(),
                        ImmutableList.of(1, 11, 4, 2, 2, 1).reverse(),
                        ImmutableList.of(3, 12, 4, 7, 7, 1).reverse(),
                        ImmutableList.of()
                ),
                ImmutableList.of(
                        ImmutableList.of(1, 2, 1, 1, 2, 11, 1),
                        ImmutableList.of(1, 2, 2, 1, 2, 4, 2),
                        ImmutableList.of(1, 3, 2, 3, 4, 1, 1),
                        ImmutableList.of(1, 1, 4, 1, 5, 1),
                        ImmutableList.of(1, 1, 3, 3, 2, 1, 1),
                        ImmutableList.of(1, 2, 1, 2, 2, 2, 2, 2),
                        ImmutableList.of(1, 1, 4, 2, 2, 3, 2),
                        ImmutableList.of(1, 6, 4, 2, 2),
                        ImmutableList.of(5, 3, 4, 3, 3),
                        ImmutableList.of(1, 5, 6, 3, 3),
                        ImmutableList.of(1, 8, 1, 4, 4),
                        ImmutableList.of(1, 3, 2, 1, 2, 4, 4),
                        ImmutableList.of(1, 2, 1, 1, 2, 3, 5),
                        ImmutableList.of(1, 1, 2, 2, 4, 6),
                        ImmutableList.of(1, 1, 3, 4, 8),
                        ImmutableList.of(3, 1, 2, 21),
                        ImmutableList.of(4, 1, 1, 2, 9, 8),
                        ImmutableList.of(1, 4, 4),
                        ImmutableList.of(1, 1, 2, 4),
                        ImmutableList.of(1, 2, 2, 4, 7, 1),
                        ImmutableList.of(2, 2, 3, 12, 2),
                        ImmutableList.of(3, 3, 2, 20),
                        ImmutableList.of(3, 2, 3, 12),
                        ImmutableList.of(3, 1, 4, 4, 9),
                        ImmutableList.of(1, 2, 5, 3, 6),
                        ImmutableList.of(4, 8, 2, 3, 1),
                        ImmutableList.of(1, 7, 4, 2, 2, 3, 1),
                        ImmutableList.of(1, 2, 4, 9, 1, 4, 1),
                        ImmutableList.of(1, 1, 1, 2, 9, 1, 3, 1),
                        ImmutableList.of(1, 1, 3, 8, 3, 2, 1),
                        ImmutableList.of(1, 1, 2, 4, 3, 3, 1),
                        ImmutableList.of(1, 1, 1, 1, 4, 3, 4, 2, 1),
                        ImmutableList.of(1, 1, 1, 2, 2, 3, 5, 2),
                        ImmutableList.of(1, 1, 1, 2, 2, 3, 1, 3, 2),
                        ImmutableList.of(1, 1, 1, 1, 2, 2, 1, 2, 1),
                        ImmutableList.of(1, 1, 3, 1, 2, 1, 1, 1),
                        ImmutableList.of(1, 4, 1, 1, 1, 1, 2),
                        ImmutableList.of(1, 3, 1, 2, 2, 1, 1),
                        ImmutableList.of(1, 2, 1, 2, 1, 2, 1),
                        ImmutableList.of(1, 1, 2, 2, 2, 2, 1),
                        ImmutableList.of(1, 1, 1, 2, 2, 1, 1),
                        ImmutableList.of(1, 2, 2, 1, 3, 1, 2),
                        ImmutableList.of(1, 2, 2, 5, 2, 2),
                        ImmutableList.of(3, 2, 3, 4),
                        ImmutableList.of(4, 7, 14, 3, 2),
                        ImmutableList.of()
                )
        );
        /*/ //small

        // .X....X.
        // .X.XX.X.
        // .X....X.
        // .XXXXXX.
        // ........

        NonoGame game = new NonoGame(
                ImmutableList.of(
                        ImmutableList.of(4),
                        ImmutableList.of(1),
                        ImmutableList.of(1, 1),
                        ImmutableList.of(1, 1),
                        ImmutableList.of(1),
                        ImmutableList.of(4),
                        ImmutableList.of()
                ),
                ImmutableList.of(
                        ImmutableList.of(1, 1),
                        ImmutableList.of(1, 2, 1),
                        ImmutableList.of(1, 1),
                        ImmutableList.of(6),
                        ImmutableList.of()
                )
        );
        //*/


        //* Human
        AtomicBoolean freeze = new AtomicBoolean(false);
        gui.mouseClickEventBus.register(new BoardClickListener(pos -> {
            if (freeze.get()) {
                logger.atInfo().log("Game is complete, input is frozen!");
                return;
            }
            logger.atInfo().log("Click @ %s", pos);
            try {
                game.toggleBoardSpace(pos);
            } catch (IllegalArgumentException e) {
            }// swallow
            gui.updateBoard(game.getBoard());
        }));

        while (!game.isComplete()) {
            // TODO do actual event handling or something
            Thread.sleep(100);
            gui.updateBoard(game.getBoard());
        }
        freeze.set(true);

        //*/

        logger.atInfo().log("Complete");
    }

    @Override
    public void run(BoardGui<Cell> gui) throws InterruptedException {
        //runFlats(gui);
        runFullNono(gui);
    }

    public static void main(String... args) throws InterruptedException {
        NonogramDriver driver = new NonogramDriver();
        driver.run(driver.getGui());
    }
}
