package minesweeper.ai;

import common.board.ReadOnlyBoard;
import common.utils.IntVector2;
import common.utils.RandomUtils;
import minesweeper.game.BoardUtils;
import minesweeper.game.Cell;

public class RandomAi implements AI {

    @Override
    public IntVector2 getMove(ReadOnlyBoard<Cell> board) {
        return RandomUtils.randomFromList(BoardUtils.getMoves(board));
    }
}
