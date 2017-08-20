package game2048.ai;

import common.board.ReadOnlyBoard;
import common.utils.RandomUtils;
import game2048.game.Cell;
import game2048.game.Move;

public class Random2048AI implements AI2048 {

    @Override
    public Move getMove(ReadOnlyBoard<Cell> board) {
        return RandomUtils.randomFromList(Move.values());
    }
}
