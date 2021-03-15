package nonogram.ai.multi_strat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import common.gui.BoardGui;
import common.utils.Flogger;
import common.utils.IntVector2;
import nonogram.game.Cell;
import nonogram.game.NonoGame;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

@SuppressWarnings("UnstableApiUsage")
public class ExhaustiveAI {
    private static final Flogger logger = Flogger.getInstance();


    /**
     * Looks for moves that 100% are required as no other moves would fit.
     */
    private static class SimpleRequiredStrategy implements MultiStratAI {
        @Override
        public ImmutableMap<IntVector2, AIFlags> generateFlags(NonoGame game, ImmutableMap<IntVector2, AIFlags> existingFlags) {
            HashMap<IntVector2, AIFlags> newFlags = new HashMap<>(existingFlags);
            for (int irow = 0; irow < game.rows.size(); irow++) {
                // block sizes + number of required white squares
                // required white squares is the number of blocks -1
                int sum = game.rows.get(irow).stream().mapToInt(s -> s).sum() + game.rows.get(irow).size() - 1;
                // TADA! this line will take up the whole row
                if (sum == game.getGameWidth()) {
                    int x = 0;
                    for (int i = 0; i < game.rows.get(irow).size(); i++) {
                        for (int j = 0; j < game.rows.get(irow).get(i); j++) {
                            IntVector2 pos = IntVector2.of(x, irow);
                            newFlags.put(pos, AIFlags.SAFE);
                            x++;
                        }
                        x++; // add blank space
                    }
                }
            }
            for (int icol = 0; icol < game.columns.size(); icol++) {
                // block sizes + number of required white squares
                // required white squares is the number of blocks -1
                int sum = game.columns.get(icol).stream().mapToInt(s -> s).sum() + game.columns.get(icol).size() - 1;
                // TADA! this line will take up the whole row
                if (sum == game.getGameHeight()) {
                    int y = 0;
                    for (int i = 0; i < game.columns.get(icol).size(); i++) {
                        for (int j = 0; j < game.columns.get(icol).get(i); j++) {
                            IntVector2 pos = IntVector2.of(icol, y);
                            newFlags.put(pos, AIFlags.SAFE);
                            y++;
                        }
                        y++; // add blank space
                    }
                }
            }
            return ImmutableMap.copyOf(newFlags);
        }
    }

    /**
     * Looks for squares requires as the only possible moves overlap.
     */
    private static class SimpleOverlapStrategy implements MultiStratAI {
        @Override
        public ImmutableMap<IntVector2, AIFlags> generateFlags(NonoGame game, ImmutableMap<IntVector2, AIFlags> existingFlags) {
            return null;
        }
    }

    private ImmutableMap<IntVector2, AIFlags> combineBlockedFlags(ImmutableMap<IntVector2, AIFlags> a, ImmutableMap<IntVector2, AIFlags> b) {
        Map<IntVector2, AIFlags> combined = new HashMap<>();
        a.entrySet().stream().filter(e -> !AIFlags.SAFE.equals(e.getValue())).forEach(e -> combined.put(e.getKey(), e.getValue()));
        b.entrySet().stream().filter(e -> !AIFlags.SAFE.equals(e.getValue())).forEach(e -> combined.put(e.getKey(), e.getValue()));
        return ImmutableMap.copyOf(combined);
    }


    public ImmutableSet<NonoGame> solve(NonoGame game, BoardGui<Cell> gui) throws InterruptedException {
        ImmutableList<MultiStratAI> strats = ImmutableList.of(
                new SimpleRequiredStrategy()
        );

        while (!game.isComplete()) {
            ImmutableMap<IntVector2, AIFlags> flags = ImmutableMap.of();
            boolean madeAMove = false;
            for (MultiStratAI strat : strats) {
                while (true) { // reuse the same strat until it doesn't find any moves
                    ImmutableMap<IntVector2, AIFlags> newFlags = strat.generateFlags(game, flags);
                    ImmutableSet<IntVector2> validMoves = newFlags.entrySet()
                            .stream()
                            .filter(entry -> AIFlags.SAFE.equals(entry.getValue()))
                            .filter(entry -> !game.getSelected().contains(entry.getKey())) // don't override
                            .map(Map.Entry::getKey)
                            .collect(toImmutableSet());
                    if (!validMoves.isEmpty()) {
                        madeAMove = true;
                        validMoves.forEach(game::toggleGameSpace);
                        gui.updateBoard(game);
                        Thread.sleep(100);
                        flags = combineBlockedFlags(flags, newFlags);
                    } else {
                        flags = combineBlockedFlags(flags, newFlags);
                        break; // no new moves, time to move on
                    }
                }
            }
            if (!madeAMove) {
                logger.atInfo().log("We tried, but we are out of moves");
                break;
            }
        }
        return ImmutableSet.of(game);
    }
}
