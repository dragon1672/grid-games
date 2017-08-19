package tribble_defense.game;

import com.google.common.collect.ImmutableBiMap;

public enum Cell {
    N0(0),
    N1(1),
    N2(2),
    N3(3),
    N4(4),
    N5(5),
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
            .build();

    @Override
    public String toString() {
        if (cellNumMap.containsKey(this)) {
            return Integer.toString(cellNumMap.get(this));
        }
        return super.toString();
    }
}
