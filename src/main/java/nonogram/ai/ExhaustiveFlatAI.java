package nonogram.ai;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import common.gui.BoardGui;
import common.utils.AsciiBoard;
import common.utils.BoardUtils;
import common.utils.Flogger;
import common.utils.IntVector2;
import nonogram.game.Cell;
import nonogram.game.FlatNonoGame;

import java.util.*;
import java.util.function.Supplier;

public class ExhaustiveFlatAI {
    private static final Flogger logger = Flogger.getInstance();

    private static class Move implements Comparable<Move> {
        private final FlatNonoGame game;
        private final Supplier<ImmutableSet<IntVector2>> possibleMoves = Suppliers.memoize(this::getPossibleMoves);

        // Game is mutable, so make sure to duplicate it before saving it as a move so possible moves doesn't get corrupted
        // opting to leave this free floating to avoid extra dups
        private Move(FlatNonoGame game) {
            this.game = game;
        }

        private ImmutableSet<IntVector2> getPossibleMoves() {
            return ExhaustiveFlatAI.getPossibleMoves(this.game);
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Move move = (Move) o;
            return Objects.equals(game.getBoard(), move.game.getBoard());
        }

        @Override
        public int hashCode() {
            return Objects.hash(game.getBoard());
        }

        @Override
        public int compareTo(Move that) {
            return Integer.compare(this.possibleMoves.get().size(), that.possibleMoves.get().size());
        }
    }

    private static ImmutableSet<IntVector2> getPossibleMoves(FlatNonoGame game) {
        List<Integer> freeColumns = new ArrayList<>(game.getColumns());
        List<Integer> freeRows = new ArrayList<>(game.getRows());
        BoardUtils.boardPositionsAsStream(game.getBoard())
                .filter(pos -> Cell.SELECTED.equals(game.getBoard().get(pos)))
                .map(pos -> pos.sub(IntVector2.of(1, 1)))
                .forEach(pos -> {
                    freeColumns.set(pos.x, freeColumns.get(pos.x) - 1);
                    freeRows.set(pos.y, freeRows.get(pos.y) - 1);
                });
        ImmutableSet.Builder<IntVector2> possibleMoves = ImmutableSet.builder();
        for (int x = 0; x < freeColumns.size(); x++) {
            if (freeColumns.get(x) <= 0) continue; // skip the empties
            for (int y = 0; y < freeRows.size(); y++) {
                if (freeRows.get(y) <= 0) continue; // skip the empties
                IntVector2 pos = IntVector2.of(x + 1, y + 1);
                if (Cell.SELECTED.equals(game.getBoard().get(pos))) continue;
                possibleMoves.add(pos);
            }
        }
        return possibleMoves.build();
    }

    private ImmutableSet<IntVector2> toggleAllPossibleMoves(FlatNonoGame game, BoardGui<Cell> gui) {
        ImmutableSet<IntVector2> possibleMoves = getPossibleMoves(game);
        for (IntVector2 pos : possibleMoves) {
            game.toggle(pos); // TOGGLE EVERYTHING!
        }
        gui.updateBoard(game.getBoard());
        return possibleMoves;
    }

    private ImmutableSet<FlatNonoGame> smartSolution(FlatNonoGame game, BoardGui<Cell> gui) {
        ImmutableSet.Builder<FlatNonoGame> winningGames = ImmutableSet.builder();
        Set<Move> seenMoves = new HashSet<>();
        TreeSet<Move> moves = new TreeSet<>();
        moves.add(new Move(game.duplicate()));

        while (!moves.isEmpty()) {
            Move move = moves.pollFirst();
            moves.remove(move);
            if (move == null || seenMoves.contains(move)) continue;
            seenMoves.add(move);
            gui.updateBoard(move.game.getBoard());

            if (move.possibleMoves.get().isEmpty()) {
                // success!
                if (move.game.isComplete()) {
                    logger.atInfo().log("Valid board solution found!\n%s", AsciiBoard.boardToString(move.game.getBoard(), c -> c == Cell.SELECTED ? 'x' : c.toString().charAt(0)));
                    gui.updateBoard(move.game.getBoard());
                    winningGames.add(move.game);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                move.possibleMoves.get().forEach(pos -> {
                    FlatNonoGame newGame = move.game.duplicate();
                    newGame.toggle(pos);
                    moves.add(new Move(newGame));
                });
            }
        }
        return winningGames.build();
    }

    public ImmutableSet<FlatNonoGame> solve(FlatNonoGame game, BoardGui<Cell> gui) {
        //toggleAllPossibleMoves(game, gui);
        return smartSolution(game, gui);
    }
}
