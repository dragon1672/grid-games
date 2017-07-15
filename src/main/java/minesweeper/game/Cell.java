package minesweeper.game;

import com.google.common.collect.ImmutableBiMap;

public enum Cell {
    EMPTY(false),
    BOMB(true),
    N8(true),
    N7(true),
    N6(true),
    N5(true),
    N4(true),
    N3(true),
    N2(true),
    N1(true),
    N0(true);

    public final boolean finalState;

    Cell(boolean finalState) {
        this.finalState = finalState;
    }

    public static final ImmutableBiMap<Cell, Integer> cellNumMap = ImmutableBiMap.<Cell, Integer>builder()
            .put(N8, 8)
            .put(N7, 7)
            .put(N6, 6)
            .put(N5, 5)
            .put(N4, 4)
            .put(N3, 3)
            .put(N2, 2)
            .put(N1, 1)
            .put(N0, 0)
            .build();

    @Override
    public String toString() {
        if (cellNumMap.containsKey(this)) {
            return Integer.toString(cellNumMap.get(this));
        }
        return super.toString();
    }
}
