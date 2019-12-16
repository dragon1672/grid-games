package minesweeper.ai;

import common.board.ReadOnlyBoard;
import common.utils.IntVector2;
import common.utils.RandomUtils;
import minesweeper.game.Cell;
import minesweeper.game.MineSweeperBoardUtils;

public class RandomMineSweeperAi implements MineSweeperAI {

    @Override
    public IntVector2 getMove(ReadOnlyBoard<Cell> board, int numBombs) {
        return RandomUtils.randomFromList(MineSweeperBoardUtils.getMoves(board));
    }
}
