package lights_out.game;

import common.board.Board;
import common.board.BoardImpl;
import common.board.ReadOnlyBoard;
import common.interfaces.Game;
import common.utils.BoardUtils;
import common.utils.IntVector2;

import java.util.Random;

public class LightsOutGame implements Game<LightsOutCell> {

    private static final Random rand = new Random();

    private final Board<LightsOutCell> board;

    private LightsOutGame(Board<LightsOutCell> board) {
        this.board = board;
    }

    public static LightsOutGame createGame(int width, int height, boolean randomize) {
        int randomizeIterations = randomize ? 100 : 0;
        return createGame(width, height, randomizeIterations);
    }

    private static LightsOutGame createGame(int width, int height, int randomizeIterations) {
        Board<LightsOutCell> board = BoardImpl.make(width, height);
        for (int i = 0; i < randomizeIterations; i++) {
            IntVector2 randomPos = IntVector2.of(rand.nextInt(width), rand.nextInt(height));
            LightsOutBoardUtils.toggleSurroundingCells(board, randomPos);
        }
        return new LightsOutGame(board);
    }

    public void makeMove(IntVector2 pos) {
        LightsOutBoardUtils.toggleSurroundingCells(board, pos);
    }

    @Override
    public boolean isComplete() {
        return BoardUtils.isUniformColor(board);
    }

    @Override
    public ReadOnlyBoard<LightsOutCell> getBoard() {
        return BoardImpl.copyOf(board);
    }
}
