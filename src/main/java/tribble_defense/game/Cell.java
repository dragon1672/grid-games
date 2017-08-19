package tribble_defense.game;

import com.google.common.collect.ImmutableBiMap;

public enum Cell {
    N0(0),
    N1(1),
    N2(2),
    N3(3),
    N4(4),
    N5(5),
    N6(6),
    N7(7),
    N8(8),
    BLOCKED(-1);

    public final int value;

    Cell(int value) {
        this.value = value;
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
