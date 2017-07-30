package popit.ai;

import common.board.ReadOnlyBoard;
import common.utils.IntVector2;
import popit.game.BlockColor;

import java.util.List;

public interface PopItAi {
    List<IntVector2> getMoves(ReadOnlyBoard<BlockColor> board);
}
