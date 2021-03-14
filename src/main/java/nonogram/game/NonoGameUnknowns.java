package nonogram.game;

import com.google.common.collect.ImmutableList;
import common.board.ReadOnlyBoard;
import common.utils.IntVector2;
import nonogram.AssignableSet;

@SuppressWarnings("UnstableApiUsage")
public class NonoGameUnknowns implements NonoGame, ReadOnlyBoard<Cell> {

    private final AssignableSet<IntVector2> selections = new AssignableSet<>();
    private final ImmutableList<Integer> columns;
    private final ImmutableList<Integer> rows;

    private final AssignableSet<Integer> satisfiedColumns = new AssignableSet<>();
    private final AssignableSet<Integer> satisfiedRows = new AssignableSet<>();

    public NonoGameUnknowns(ImmutableList<Integer> columns, ImmutableList<Integer> rows) {
        if (columns.isEmpty() || rows.isEmpty()) {
            throw new IllegalArgumentException("columns and rows must be provided");
        }
        for (int i : columns) {
            if (i > rows.size()) throw new IllegalArgumentException(String.format("column value %d is impossible", i));
            if (i > 9) throw new IllegalArgumentException("max size of 9 (image assets)");
        }
        for (int i : rows) {
            if (i > columns.size()) throw new IllegalArgumentException(String.format("row value %d is impossible", i));
            if (i > 9) throw new IllegalArgumentException("max size of 9 (image assets)");
        }
        this.columns = columns;
        this.rows = rows;
        // initialize columns/rows
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i) == 0) satisfiedColumns.add(i + 1);
        }
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i) == 0) satisfiedRows.add(i + 1);
        }
    }

    @Override
    public Cell get(int x, int y) {
        if (x == 0 && y == 0) {
            return Cell.N0;
        }
        // Headers
        if (x == 0) {
            return Cell.cellNumMap.inverse().get(rows.get(y - 1));
        }
        if (y == 0) {
            return Cell.cellNumMap.inverse().get(columns.get(x - 1));
        }
        IntVector2 pos = IntVector2.of(x, y);
        return selections.contains(pos) ? Cell.SELECTED : Cell.N0;
    }

    @Override
    public int getWidth() {
        return columns.size() + 1;
    }

    @Override
    public int getHeight() {
        return rows.size() + 1;
    }

    @Override
    public boolean isComplete() {
        return satisfiedColumns.size() == columns.size() && satisfiedRows.size() == rows.size();
    }

    @Override
    public ReadOnlyBoard<Cell> getBoard() {
        return this;
    }

    @Override
    public ImmutableList<Integer> getColumns() {
        return columns;
    }

    @Override
    public ImmutableList<Integer> getRows() {
        return rows;
    }

    public void toggle(IntVector2 pos) {
        if (!isValidPos(pos) || pos.x == 0 || pos.y == 0) {
            throw new IllegalArgumentException(String.format("Invalid position: %s", pos));
        }
        // Toggle
        selections.updateContains(pos, !selections.contains(pos));

        // Update satisfied rows
        int xCount = 0;
        for (int x = 0; x < getWidth(); x++) {
            if (selections.contains(IntVector2.of(x, pos.y))) xCount++;
        }
        satisfiedRows.updateContains(pos.y, xCount == rows.get(pos.y - 1));

        // Update satisfied columns
        int yCount = 0;
        for (int y = 0; y < getHeight(); y++) {
            if (selections.contains(IntVector2.of(pos.x, y))) yCount++;
        }
        satisfiedColumns.updateContains(pos.x, yCount == columns.get(pos.x - 1));
    }
}
