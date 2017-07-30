package flood_it.game;

import com.google.common.collect.ImmutableList;
import common.board.Board;
import common.board.BoardImpl;
import common.board.ReadOnlyBoard;
import common.interfaces.Game;
import common.utils.BoardUtils;
import common.utils.IntVector2;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Flood It Game
 */
public class FloodIt implements Game<FloodColor> {
    private final Board<FloodColor> board;
    private final ImmutableList<FloodColor> validBlocks;

    public static final IntVector2 MOVE_POS = IntVector2.of(0, 0);

    private FloodIt(Board<FloodColor> board, List<FloodColor> validBlocks) {
        this.board = board;
        this.validBlocks = ImmutableList.copyOf(validBlocks);
    }

    private void randomizeBoard() {
        Random rand = new Random();
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                FloodColor randomBlock = validBlocks.get(rand.nextInt(validBlocks.size()));
                board.set(randomBlock, x, y);
            }
        }
    }

    public void floodColor(FloodColor color) {
        checkArgument(validBlocks.contains(color), "provided color must be in validBlocks " + Arrays.toString(validBlocks.toArray()));
        Set<IntVector2> colorsToSwap = BoardUtils.getConnectedCells(board, MOVE_POS);
        colorsToSwap.forEach(pos -> board.set(color, pos.x, pos.y));
    }

    @SuppressWarnings("SameParameterValue")
    public static FloodIt startGame(int width, int height) {
        FloodIt game = new FloodIt(BoardImpl.make(width, height), ImmutableList.of(FloodColor.RED,
                FloodColor.YELLOW,
                FloodColor.GREEN,
                FloodColor.LIGHT_BLUE,
                FloodColor.BLUE,
                FloodColor.PURPLE));
        game.randomizeBoard();
        return game;
    }

    @Override
    public ReadOnlyBoard<FloodColor> getBoard() {
        return board;
    }

    @Override
    public boolean isComplete() {
        return BoardUtils.isUniformColor(board);
    }
}