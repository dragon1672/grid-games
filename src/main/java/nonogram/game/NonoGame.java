package nonogram.game;

import com.google.common.collect.ImmutableList;
import common.AssignableSet;
import common.board.ReadOnlyBoard;
import common.interfaces.Game;
import common.utils.Flogger;
import common.utils.IntVector2;

import java.util.AbstractCollection;
import java.util.Collections;
import java.util.Set;

public class NonoGame implements Game<Cell>, ReadOnlyBoard<Cell> {
    private static final Flogger logger = Flogger.getInstance();

    public final ImmutableList<ImmutableList<Integer>> columns;
    public final ImmutableList<ImmutableList<Integer>> rows;

    private final int columnHeight;
    private final int rowWidth;

    private final AssignableSet<Integer> satisfiedColumns = new AssignableSet<>();
    private final AssignableSet<Integer> satisfiedRows = new AssignableSet<>();
    private final AssignableSet<IntVector2> selected = new AssignableSet<>();

    public NonoGame(ImmutableList<ImmutableList<Integer>> columns, ImmutableList<ImmutableList<Integer>> rows) {
        if (columns.isEmpty() || rows.isEmpty()) {
            throw new IllegalArgumentException("Must have at least some columns/rows");
        }
        this.columns = columns;
        this.rows = rows;
        columnHeight = columns.stream().mapToInt(AbstractCollection::size).max().orElse(0);
        rowWidth = rows.stream().mapToInt(AbstractCollection::size).max().orElse(0);

        // initialize columns/rows
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).isEmpty()) satisfiedColumns.add(i);
        }
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).isEmpty()) satisfiedRows.add(i);
        }
    }

    // Private for duplication, no verification checks.
    private NonoGame(ImmutableList<ImmutableList<Integer>> columns, ImmutableList<ImmutableList<Integer>> rows, int columnHeight, int rowWidth, Set<Integer> satisfiedColumns, Set<Integer> satisfiedRows, Set<IntVector2> selected) {
        this.columns = columns;
        this.rows = rows;
        this.columnHeight = columnHeight;
        this.rowWidth = rowWidth;
        this.satisfiedColumns.addAll(satisfiedColumns);
        this.satisfiedRows.addAll(satisfiedRows);
        this.selected.addAll(selected);
    }

    // operates in game space
    public void toggleBoardSpace(IntVector2 pos) {
        toggleGameSpace(pos.sub(IntVector2.of(rowWidth, columnHeight)));
    }

    private boolean validGameSpace(IntVector2 pos) {
        return 0 <= pos.x && pos.x < columns.size()
                && 0 <= pos.y && pos.y < rows.size();
    }

    private boolean rowSatisfied(int y) {
        int satisfiedCount = 0;
        int blockSize = 0;
        int columnIndex = 0;
        for (int x = 0; x < getWidth(); x++) {
            IntVector2 pos = IntVector2.of(x, y);
            if (selected.contains(pos)) blockSize++;
            else {
                if (rows.get(y).isEmpty()) return false;
                if (blockSize == rows.get(y).get(columnIndex)) {
                    blockSize = 0;
                    satisfiedCount++;
                    columnIndex++;
                    if (columnIndex >= rows.get(y).size()) break;
                } else {
                    if (blockSize > 0) return false; // doesn't match the required block pattern
                }
            }
        }
        return satisfiedCount == rows.get(y).size();
    }

    private boolean columnSatisfied(int x) {
        int satisfiedCount = 0;
        int blockSize = 0;
        int rowIndex = 0;
        for (int y = 0; y < getHeight(); y++) {
            IntVector2 pos = IntVector2.of(x, y);
            if (selected.contains(pos)) blockSize++;
            else {
                if (columns.get(x).isEmpty()) return false;
                if (blockSize == columns.get(x).get(rowIndex)) {
                    blockSize = 0;
                    satisfiedCount++;
                    rowIndex++;
                    if (rowIndex >= columns.get(x).size()) break;
                } else {
                    if (blockSize > 0) return false; // doesn't match the required block pattern
                }
            }
        }
        return satisfiedCount == columns.get(x).size();
    }

    public void toggleGameSpace(IntVector2 pos) {
        if (!validGameSpace(pos))
            throw new IllegalArgumentException(String.format("invalid pos %s", pos));
        selected.updateContains(pos, !selected.contains(pos)); // toggle

        // Update satisfied rows
        satisfiedRows.updateContains(pos.y, rowSatisfied(pos.y));
        satisfiedColumns.updateContains(pos.x, columnSatisfied(pos.x));
    }

    @Override
    public boolean isComplete() {
        return satisfiedColumns.size() == columns.size() && satisfiedRows.size() == rows.size();
    }

    @Override
    public ReadOnlyBoard<Cell> getBoard() {
        return this;
    }

    public int getGameWidth() {
        return columns.size();
    }

    public int getGameHeight() {
        return rows.size();
    }

    public Set<IntVector2> getSelected() {
        return Collections.unmodifiableSet(selected);
    }

    // ReadOnlyBoard glues together column headers & game space
    @Override
    public Cell get(int x, int y) {
        if (x < rowWidth && y < columnHeight) {
            // The Dead Zone!
            return Cell.N0;
        }
        IntVector2 gameSpace = IntVector2.of(x - rowWidth, y - columnHeight);
        if (x < rowWidth) {
            int size = rows.get(gameSpace.y).size();
            int padding = rowWidth - size;
            int index = x - padding; // padding pushes values to align on the right
            if (0 <= index && index < size) return num2Cell(rows.get(gameSpace.y).get(index));
            return Cell.N0;
        }
        if (y < columnHeight) {
            int size = columns.get(gameSpace.x).size();
            int index = Math.abs(y + 1 - columnHeight); // flip to right justify
            if (0 <= index && index < size) return num2Cell(columns.get(gameSpace.x).get(index));
            return Cell.N0;
        }
        // Not a header so pull from game board
        return selected.contains(gameSpace) ? Cell.SELECTED : Cell.N0;
    }

    private Cell num2Cell(int num) {
        return Cell.cellNumMap.inverse().getOrDefault(num, Cell.UNKNOWN);
    }

    @Override
    public int getWidth() {
        return columns.size() + rowWidth;
    }

    @Override
    public int getHeight() {
        return rows.size() + columnHeight;
    }

    @Override
    public boolean equals(Object that) {
        return this == that || that instanceof ReadOnlyBoard && isEqualTo((ReadOnlyBoard<?>) that);
    }

    @Override
    public int hashCode() {
        return getHashCode();
    }

    @Override
    public String toString() {
        return asString();
    }

    public NonoGame duplicate() {
        return new NonoGame(columns, rows, columnHeight, rowWidth, satisfiedColumns, satisfiedRows, selected);
    }
}
