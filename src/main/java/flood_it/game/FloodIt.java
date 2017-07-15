package flood_it.game;

import com.google.common.collect.ImmutableList;
import common.board.Board;
import common.board.BoardImpl;
import common.board.ReadOnlyBoard;
import common.interfaces.Game;
import common.utils.IntVector2;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Flood It Game
 */
public class FloodIt implements Game {
    private final Board<BlockColor> board;
    private final ImmutableList<BlockColor> validBlocks;

    private FloodIt(Board<BlockColor> board, List<BlockColor> validBlocks) {
        this.board = board;
        this.validBlocks = ImmutableList.copyOf(validBlocks);
    }

    private void randomizeBoard() {
        Random rand = new Random();
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                BlockColor randomBlock = validBlocks.get(rand.nextInt(validBlocks.size()));
                board.set(randomBlock, x, y);
            }
        }
    }

    public void floodColor(BlockColor color) {
        checkArgument(validBlocks.contains(color), "provided color must be in validBlocks " + Arrays.toString(validBlocks.toArray()));
        Set<IntVector2> colorsToSwap = BoardUtilities.getConnectedColors(board);
        colorsToSwap.forEach(pos -> board.set(color, pos.x, pos.y));
    }

    @SuppressWarnings("SameParameterValue")
    public static FloodIt startGame(int width, int height) {
        FloodIt game = new FloodIt(BoardImpl.make(width, height), ImmutableList.of(BlockColor.RED,
                BlockColor.YELLOW,
                BlockColor.GREEN,
                BlockColor.LIGHT_BLUE,
                BlockColor.BLUE,
                BlockColor.PURPLE));
        game.randomizeBoard();
        return game;
    }

    @Override
    public ReadOnlyBoard<BlockColor> getBoard() {
        return board;
    }

    @Override
    public boolean isComplete() {
        return BoardUtilities.isUniformColor(board);
    }
}