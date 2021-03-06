package nonogram.game;

import com.google.common.collect.ImmutableList;
import common.interfaces.Game;
import common.utils.IntVector2;

public interface FlatNonoGame extends Game<Cell> {
    ImmutableList<Integer> getColumns();

    ImmutableList<Integer> getRows();

    void toggle(IntVector2 pos);

    FlatNonoGame duplicate();
}
