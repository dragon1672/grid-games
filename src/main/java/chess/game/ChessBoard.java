package chess.game;

import common.board.Board;
import common.board.ReadOnlyBoard;
import common.interfaces.Game;

/**
 * MineSweeper game
 */
public class ChessBoard implements Game<ChessBoardCell> {
    private Board<MutableChessBoardCell> board;


    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public ReadOnlyBoard<ChessBoardCell> getBoard() {
        return null; //TODO: figure out a clean casting method(Board<ChessBoardCell>)board;
    }
}
