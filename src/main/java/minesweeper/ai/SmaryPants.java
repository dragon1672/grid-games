package minesweeper.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import common.board.ReadOnlyBoard;
import common.utils.Flogger;
import common.utils.IntVector2;
import common.utils.RandomUtils;
import minesweeper.ai.GraphyMineSweeperAi.Danger;
import minesweeper.game.Cell;
import minesweeper.game.MineSweeperBoardUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

public class SmaryPants implements MineSweeperAI {
    private static final Flogger logger = Flogger.getInstance();

    @Override
    public IntVector2 getMove(ReadOnlyBoard<Cell> board, int numBombs) {
        ImmutableMap<IntVector2, Danger> cellStateMap = GraphyMineSweeperAi.flagBoard(board);
        logger.atInfo().log("map size %d", cellStateMap.size());

        Optional<IntVector2> safeMove = cellStateMap.keySet().stream().filter(pos -> cellStateMap.get(pos) == Danger.SAFE).findFirst();
        if (safeMove.isPresent()) {
            logger.atInfo().log("Picking safe move %s", safeMove.get());
            return safeMove.get();
        }

        ImmutableList<ImmutableSet<IntVector2>> possibleBombLocations = getPossibleBombLocations(cellStateMap, board, numBombs);
        // Map each unknown to a probability

        Map<IntVector2, Float> mineProbabilities = new HashMap<>();
        cellStateMap.entrySet().stream()
                .filter(entry -> entry.getValue() == Danger.Unknown)
                .forEach(entry -> {
                    float probability = (float) possibleBombLocations.stream().filter(bombPositions -> bombPositions.contains(entry.getKey())).count() / (float) possibleBombLocations.size();
                    mineProbabilities.put(entry.getKey(), probability);
                });
        Optional<Map.Entry<IntVector2, Float>> safestOption = mineProbabilities.entrySet().stream().min((a, b) -> Float.compare(a.getValue(), b.getValue()));
        if (safestOption.isPresent()) {
            logger.atInfo().log("Picking safest option with a %f chance to be a bomb from %d possible states", safestOption.get().getValue(), mineProbabilities.size());
            return safestOption.get().getKey();
        }

        // looks like everything is a bomb?!?
        logger.atInfo().log("Unable to make a smart choice, might as well pick something random");
        return RandomUtils.randomFromList(MineSweeperBoardUtils.getMoves(board));
    }

    ImmutableList<ImmutableSet<IntVector2>> getPossibleBombLocations(ImmutableMap<IntVector2, Danger> knownDangers, ReadOnlyBoard<Cell> board, int numMines) {
        ImmutableList.Builder<ImmutableSet<IntVector2>> values = ImmutableList.builder();
        for (Map.Entry<IntVector2, Danger> entry : knownDangers.entrySet()) {
            if (!entry.getValue().equals(Danger.Unknown)) {
                continue;
            }
            Map<IntVector2, Danger> tmpNewAssumedDangers = new HashMap<>();
            tmpNewAssumedDangers.putAll(knownDangers);
            tmpNewAssumedDangers.put(entry.getKey(), Danger.BOMB); // override with bomb
            ImmutableMap<IntVector2, Danger> newDangers = GraphyMineSweeperAi.flagBoard(ImmutableMap.copyOf(tmpNewAssumedDangers), board);
            if (newDangers.values().stream().filter(Danger.BOMB::equals).count() > numMines) {
                continue;
            }
            if (newDangers.values().stream().anyMatch(Danger.Unknown::equals)) {
                values.addAll(getPossibleBombLocations(newDangers, board, numMines));
            } else {
                ImmutableSet<IntVector2> minePos = newDangers.keySet().stream().filter(pos -> newDangers.get(pos).equals(Danger.BOMB)).collect(toImmutableSet());
                values.add(minePos);
            }
        }
        return values.build();
        // Limit board to only cells that have a number by them
        // Foreach unknown cell, assume it is a bomb and determine board state
        // if board has unknowns repeat until reached bomb limit of assumed/known bombs
        // Discard state if bomb count exceeds limit
        // Only track possible state when there are no unknowns
    }
}
