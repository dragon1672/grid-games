package chess.ai;

import common.board.ReadOnlyBoard;
import common.utils.IntVector2;
import common.utils.RandomUtils;
import minesweeper.ai.*;
import minesweeper.game.Cell;
import minesweeper.game.MineSweeperBoardUtils;

public class RandomMineSweeperAi implements minesweeper.ai.MineSweeperAI {

    @Override
    public IntVector2 getMove(ReadOnlyBoard<Cell> board) {
        return RandomUtils.randomFromList(MineSweeperBoardUtils.getMoves(board));
    }
}
