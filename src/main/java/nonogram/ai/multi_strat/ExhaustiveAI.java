package nonogram.ai.multi_strat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import common.gui.BoardGui;
import common.utils.Flogger;
import common.utils.IntVector2;
import nonogram.game.Cell;
import nonogram.game.NonoGame;

import java.util.*;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

@SuppressWarnings("UnstableApiUsage")
public class ExhaustiveAI {
    private static final Flogger logger = Flogger.getInstance();


    /**
     * Looks for moves that 100% are required as no other moves would fit.
     */
    private static class DummyRandom implements MultiStratAI {
        @Override
        public ImmutableMap<IntVector2, AIFlags> generateFlags(NonoGame game, ImmutableMap<IntVector2, AIFlags> existingFlags) {
            List<IntVector2> possibleMoves = new ArrayList<>();
            Set<IntVector2> currentSelected = game.getSelected();
            for (int x = 0; x < game.getGameWidth(); x++) {
                for (int y = 0; y < game.getGameHeight(); y++) {
                    IntVector2 pos = IntVector2.of(x, y);
                    if (!currentSelected.contains(pos)) possibleMoves.add(pos);
                }
            }
            IntVector2 move = possibleMoves.get(new Random().nextInt(possibleMoves.size()));
            return ImmutableMap.of(move, AIFlags.SAFE);
        }
    }

    /**
     * Looks for moves that 100% are required as no other moves would fit.
     */
    private static class RequiredStrategy implements MultiStratAI {
        @Override
        public ImmutableMap<IntVector2, AIFlags> generateFlags(NonoGame game, ImmutableMap<IntVector2, AIFlags> existingFlags) {
            return null;
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

    private boolean makeMoves(ImmutableMap<IntVector2, AIFlags> flags, NonoGame game, BoardGui<Cell> gui) throws InterruptedException {
        ImmutableSet<IntVector2> validMoves = flags.entrySet()
                .stream()
                .filter(entry -> AIFlags.SAFE.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(toImmutableSet());
        if (!validMoves.isEmpty()) {
            for (IntVector2 move : validMoves) {
                game.toggleGameSpace(move);
            }
            gui.updateBoard(game);
            Thread.sleep(100);
            return true;
        }
        return false;
    }

    private ImmutableMap<IntVector2, AIFlags> combineBlockedFlags(ImmutableMap<IntVector2, AIFlags> a, ImmutableMap<IntVector2, AIFlags> b) {
        Map<IntVector2, AIFlags> combined = new HashMap<>();
        a.entrySet().stream().filter(e -> !AIFlags.SAFE.equals(e.getValue())).forEach(e -> combined.put(e.getKey(), e.getValue()));
        b.entrySet().stream().filter(e -> !AIFlags.SAFE.equals(e.getValue())).forEach(e -> combined.put(e.getKey(), e.getValue()));
        return ImmutableMap.copyOf(combined);
    }


    public ImmutableSet<NonoGame> solve(NonoGame game, BoardGui<Cell> gui) throws InterruptedException {
        ImmutableList<MultiStratAI> strats = ImmutableList.of(
                new DummyRandom()
                //new RequiredStrategy(),
                //new SimpleOverlapStrategy()
        );

        while (!game.isComplete()) {
            ImmutableMap<IntVector2, AIFlags> flags = ImmutableMap.of();
            for (MultiStratAI strat : strats) {
                while (true) { // reuse the same strat until it doesn't find any moves
                    ImmutableMap<IntVector2, AIFlags> newFlags = strat.generateFlags(game, flags);
                    ImmutableSet<IntVector2> validMoves = newFlags.entrySet()
                            .stream()
                            .filter(entry -> AIFlags.SAFE.equals(entry.getValue()))
                            .map(Map.Entry::getKey)
                            .collect(toImmutableSet());
                    if (!validMoves.isEmpty()) {
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
        }
        return ImmutableSet.of(game);
    }
}
