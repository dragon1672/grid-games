package snek;

import common.gui.BoardGui;
import common.interfaces.Runner;
import common.utils.Flogger;
import common.utils.IntVector2;
import snek.ai.AStarySnekPlayer;
import snek.ai.SnekPlayer;
import snek.game.SnekCell;
import snek.game.SnekGame;

import java.awt.*;

/**
 * Online example: https://www.chiark.greenend.org.uk/~sgtatham/puzzles/js/mines.html
 */
public class SnekDriver implements Runner<SnekCell> {
    private static final Flogger logger = Flogger.getInstance();

    public static Color cell2Color(SnekCell snekCell) {
        switch (snekCell) {
            case EMPTY:
                return Color.BLACK;
            case APPLE:
                return Color.RED;
            case BODY:
                return Color.cyan;
            case HEAD:
                return Color.GREEN;
        }
        throw new IllegalArgumentException("wat");
    }

    @Override
    public BoardGui<SnekCell> getGui() {
        return BoardGui.createColorBoard(SnekDriver::cell2Color, 1);
    }

    @Override
    public void run(BoardGui<SnekCell> gui) {
        logger.atInfo().log("Staring game");

        // Create Game board
        SnekGame game = new SnekGame(10, 10, IntVector2.of(3, 5), IntVector2.of(7, 5), 5, 2);
        //SnekPlayer ai = new RandomSnekPlayer();
        SnekPlayer ai = new AStarySnekPlayer();

        game.registerOnChange(gui::updateBoard);
        gui.updateBoard(game.getBoard());

        ai.play(game);

        logger.atInfo().log("Complete");
    }

    public static void main(String... args) {
        SnekDriver driver = new SnekDriver();
        driver.run(driver.getGui());
    }
}
