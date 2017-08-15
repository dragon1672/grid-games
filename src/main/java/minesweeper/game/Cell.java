package minesweeper.game;

import com.google.common.collect.ImmutableBiMap;

public enum Cell {
    N0(true, 0),
    N1(true, 1),
    N2(true, 2),
    N3(true, 3),
    N4(true, 4),
    N5(true, 5),
    N6(true, 6),
    N7(true, 7),
    N8(true, 8),
    BOMB(true, -1),
    EMPTY(false, -1);

    public final boolean finalState;
    public final int numAdjacentBombs;

    Cell(boolean finalState, int numAdjacentBombs) {
        this.finalState = finalState;
        this.numAdjacentBombs = numAdjacentBombs;
    }

    public static final ImmutableBiMap<Cell, Integer> cellNumMap = ImmutableBiMap.<Cell, Integer>builder()
            .put(N0, 0)
            .put(N1, 1)
            .put(N2, 2)
            .put(N3, 3)
            .put(N4, 4)
            .put(N5, 5)
            .put(N6, 6)
            .put(N7, 7)
            .put(N8, 8)
            .build();

    @Override
    public String toString() {
        if (cellNumMap.containsKey(this)) {
            return Integer.toString(cellNumMap.get(this));
        }
        return super.toString();
    }
}
