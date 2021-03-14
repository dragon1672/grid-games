package nonogram.game;

import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import common.board.BoardImpl;
import common.board.ReadOnlyBoard;
import common.utils.BoardUtils;
import common.utils.IntVector2;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableSet.toImmutableSet;

@SuppressWarnings("UnstableApiUsage")
public class NonoGameKnownSolution implements NonoGame, ReadOnlyBoard<Cell> {

    private final Set<IntVector2> selections = new HashSet<>();
    private LoadingCache<IntVector2, Cell> headerMemo = CacheBuilder.newBuilder()
            .build(CacheLoader.from(this::extractHeader));
    private Supplier<ImmutableList<Integer>> columns = Suppliers.memoize(() ->
            IntStream.rangeClosed(1, getWidth())
                    .mapToObj(x -> IntVector2.of(x, 0))
                    .map(headerMemo::getUnchecked)
                    .map(c -> c.value)
                    .collect(toImmutableList()));
    private Supplier<ImmutableList<Integer>> rows = Suppliers.memoize(() ->
            IntStream.rangeClosed(1, getHeight())
                    .mapToObj(y -> IntVector2.of(0, y))
                    .map(headerMemo::getUnchecked)
                    .map(c -> c.value)
                    .collect(toImmutableList()));

    private final ReadOnlyBoard<Boolean> solution;
    private final ImmutableSet<IntVector2> solutionPositions;

    public NonoGameKnownSolution(ReadOnlyBoard<Boolean> solution) {
        // Make an immutable copy
        this.solution = BoardImpl.copyOf(solution);
        // map required positions into a set and map to game space
        solutionPositions = BoardUtils.boardPositionsAsStream(solution).filter(solution::get).map(pos -> pos.add(IntVector2.ONE_ONE)).collect(toImmutableSet());
    }

    private NonoGameKnownSolution(ReadOnlyBoard<Boolean> solution, ImmutableSet<IntVector2> solutionPositions) {
        this.solution = solution;
        this.solutionPositions = solutionPositions;
    }

    private Cell extractHeader(IntVector2 start) {
        IntVector2 dir;
        if (start.x == 0) {
            dir = IntVector2.X_DIR;
        } else if (start.y == 0) {
            dir = IntVector2.Y_DIR;
        } else {
            throw new IllegalArgumentException(String.format("position %s isn't a header", start));
        }

        int sum = 0;
        IntVector2 pos = start;
        while (isValidPos(pos = pos.add(dir))) {
            if (solutionPositions.contains(pos)) {
                sum++;
            }
        }
        return Cell.cellNumMap.inverse().get(sum);
    }

    @Override
    public Cell get(int x, int y) {
        if (x == 0 && y == 0) {
            return Cell.N0;
        }
        IntVector2 pos = IntVector2.of(x, y);
        // getting a header
        if (x == 0) {
            return headerMemo.getUnchecked(pos);
        }
        if (y == 0) {
            return headerMemo.getUnchecked(pos);
        }
        // Sub 1 to map coords to solution space
        return selections.contains(pos) ? Cell.SELECTED : Cell.N0;
    }

    @Override
    public int getWidth() {
        return solution.getWidth() + 1;
    }

    @Override
    public int getHeight() {
        return solution.getHeight() + 1;
    }

    @Override
    public boolean isComplete() {
        return Sets.symmetricDifference(solutionPositions, selections).isEmpty();
    }

    @Override
    public ReadOnlyBoard<Cell> getBoard() {
        return this;
    }

    @Override
    public ImmutableList<Integer> getColumns() {
        return columns.get();
    }

    @Override
    public ImmutableList<Integer> getRows() {
        return rows.get();
    }

    @Override
    public void toggle(IntVector2 pos) {
        if (!isValidPos(pos) || pos.x == 0 || pos.y == 0) {
            throw new IllegalArgumentException(String.format("Invalid position: %s", pos));
        }
        if (selections.contains(pos)) {
            selections.remove(pos);
        } else {
            selections.add(pos);
        }
    }

    @Override
    public NonoGame duplicate() {
        NonoGameKnownSolution copy = new NonoGameKnownSolution(solution, solutionPositions);
        copy.selections.addAll(selections);
        // override caches with the same caches
        copy.rows = rows;
        copy.columns = columns;
        copy.headerMemo = headerMemo;
        return copy;
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
}
