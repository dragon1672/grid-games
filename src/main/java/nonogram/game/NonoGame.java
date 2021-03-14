package nonogram.game;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import common.board.Board;
import common.board.BoardImpl;
import common.board.ReadOnlyBoard;
import common.interfaces.Game;
import common.utils.BoardUtils;
import common.utils.IntVector2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

@SuppressWarnings("UnstableApiUsage")
public class NonoGame implements Board<Cell>, Game<Cell> {

    private final Set<IntVector2> selections = new HashSet<>();
    private final Map<IntVector2, Cell> cashedHeaders = new HashMap<>();
    private final ReadOnlyBoard<Boolean> solution;
    private final ImmutableSet<IntVector2> solutionPositions;

    public NonoGame(ReadOnlyBoard<Boolean> solution) {
        // Make an immutable copy
        this.solution = BoardImpl.copyOf(solution);
        // map required positions into a set and map to game space
        solutionPositions = BoardUtils.boardPositionsAsStream(solution).filter(solution::get).map(pos -> pos.add(IntVector2.ONE_ONE)).collect(toImmutableSet());
    }

    @Override
    public void set(Cell block, int x, int y) {
        throw new UnsupportedOperationException("Use toggle not set to change values");
    }

    private Cell extractHeader(IntVector2 start, IntVector2 dir) {
        return cashedHeaders.computeIfAbsent(start, unused -> {
            int sum = 0;
            IntVector2 pos = start;
            while (isValidPos(pos = pos.add(dir))) {
                if (solutionPositions.contains(pos)) {
                    sum++;
                }
            }
            return Cell.cellNumMap.inverse().get(sum);
        });
    }

    @Override
    public Cell get(int x, int y) {
        if (x == 0 && y == 0) {
            return Cell.N0;
        }
        IntVector2 pos = IntVector2.of(x, y);
        // getting a header
        if (x == 0) {
            return extractHeader(pos, IntVector2.X_DIR);
        }
        if (y == 0) {
            return extractHeader(pos, IntVector2.Y_DIR);
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
}
