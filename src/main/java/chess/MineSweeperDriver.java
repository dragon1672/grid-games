package chess;

import com.google.common.collect.ImmutableMap;
import common.gui.BoardGui;
import common.interfaces.Runner;
import common.utils.AsciiBoard;
import common.utils.Flogger;
import common.utils.IntVector2;
import minesweeper.ai.MineSweeperAI;
import minesweeper.ai.SafeBetAI;
import minesweeper.game.Cell;
import minesweeper.game.MineSweeper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MineSweeperDriver {
    private static final Flogger logger = Flogger.getInstance();



    public static void main(String... args) throws InterruptedException {
        logger.atInfo().log("TODO still gotta figure this one out");
    }
}
