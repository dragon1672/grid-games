package nonogram.game;

import com.google.common.collect.ImmutableList;
import common.board.ReadOnlyBoard;
import common.interfaces.Game;
import common.utils.Flogger;
import common.utils.IntVector2;
import nonogram.AssignableSet;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.AbstractCollection;
import java.util.HashSet;
import java.util.Set;

public class NonoGame implements Game<Cell>, ReadOnlyBoard<Cell> {
    private static final Flogger logger = Flogger.getInstance();

    public final ImmutableList<ImmutableList<Integer>> columns;
    public final ImmutableList<ImmutableList<Integer>> rows;

    private final int columnHeight;
    private final int rowWidth;

    private final AssignableSet<Integer> satisfiedColumns = new AssignableSet<>();
    private final AssignableSet<Integer> satisfiedRows = new AssignableSet<>();
    private final Set<IntVector2> selected = new HashSet<>();

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

    // operates in game space
    public void toggle(IntVector2 pos) {
        throw new NotImplementedException();
    }

    public NonoGame duplicate() {
        throw new NotImplementedException();
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public ReadOnlyBoard<Cell> getBoard() {
        return this;
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
}
