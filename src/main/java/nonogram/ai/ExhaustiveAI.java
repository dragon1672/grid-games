package nonogram.ai;

import com.google.common.collect.ImmutableSet;
import common.gui.BoardGui;
import common.utils.BoardUtils;
import common.utils.IntVector2;
import nonogram.game.Cell;
import nonogram.game.NonoGame;

import java.util.ArrayList;
import java.util.List;

public class ExhaustiveAI {

    private ImmutableSet<IntVector2> getPossibleMoves(NonoGame game) {
        List<Integer> freeColumns = new ArrayList<>(game.getColumns());
        List<Integer> freeRows = new ArrayList<>(game.getRows());
        BoardUtils.boardPositionsAsStream(game.getBoard())
                .filter(pos -> Cell.SELECTED.equals(game.getBoard().get(pos)))
                .forEach(pos -> {
                    freeColumns.set(pos.x, freeColumns.get(pos.x) - 1);
                    freeRows.set(pos.y, freeRows.get(pos.y) - 1);
                });
        ImmutableSet.Builder<IntVector2> possibleMoves = ImmutableSet.builder();
        for (int x = 0; x < freeColumns.size(); x++) {
            if (freeColumns.get(x) <= 0) continue; // skip the empties
            for (int y = 0; y < freeRows.size(); y++) {
                if (freeRows.get(y) <= 0) continue; // skip the empties
                possibleMoves.add(IntVector2.of(x + 1, y + 1));
            }
        }
        return possibleMoves.build();
    }

    public ImmutableSet<IntVector2> getSolution(NonoGame game, BoardGui<Cell> gui) {
        ImmutableSet<IntVector2> possibleMoves = getPossibleMoves(game);
        for (IntVector2 pos : possibleMoves) {
            game.toggle(pos); // TOGGLE EVERYTHING!
        }
        gui.updateBoard(game.getBoard());
        return possibleMoves;
    }
}
