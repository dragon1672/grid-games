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

    private static ImmutableList<ImmutableSet<IntVector2>> getPossibleBombLocations(ImmutableMap<IntVector2, Danger> knownDangers, ReadOnlyBoard<Cell> board, int numMines) {
        ImmutableList.Builder<ImmutableSet<IntVector2>> values = ImmutableList.builder();
        for (Map.Entry<IntVector2, Danger> entry : knownDangers.entrySet()) {
            if (!entry.getValue().equals(Danger.Unknown)) {
                continue;
            }
            Map<IntVector2, Danger> tmpNewAssumedDangers = new HashMap<>(knownDangers);
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
    }

    private static Optional<Map.Entry<IntVector2, Float>> calculateLowestProbFromUnknowns(ImmutableMap<IntVector2, Danger> cellStateMap, ReadOnlyBoard<Cell> board, int numBombs) {
        ImmutableList<ImmutableSet<IntVector2>> possibleBombLocations = getPossibleBombLocations(cellStateMap, board, numBombs);
        // Map each unknown to a probability

        Map<IntVector2, Float> mineProbabilities = new HashMap<>();
        cellStateMap.entrySet().stream()
                .filter(entry -> entry.getValue() == Danger.Unknown)
                .forEach(entry -> {
                    float probability = (float) possibleBombLocations.stream().filter(bombPositions -> bombPositions.contains(entry.getKey())).count() / (float) possibleBombLocations.size();
                    mineProbabilities.put(entry.getKey(), probability);
                });
        Optional<Map.Entry<IntVector2, Float>> lowestProb = mineProbabilities.entrySet().stream().min((a, b) -> Float.compare(a.getValue(), b.getValue()));
        lowestProb.ifPresent(intVector2FloatEntry -> logger.atInfo().log("Determined safest option of %s with a %f chance to be a bomb from %d possible states", intVector2FloatEntry.getKey(), intVector2FloatEntry.getValue(), possibleBombLocations.size()));
        return lowestProb;
    }

    @Override
    public IntVector2 getMove(ReadOnlyBoard<Cell> board, int numBombs) {
        if (MineSweeperBoardUtils.isClear(board)) {
            return RandomUtils.randomFromList(MineSweeperBoardUtils.getMoves(board));
        }
        ImmutableMap<IntVector2, Danger> cellStateMap = GraphyMineSweeperAi.flagBoard(board);

        Optional<IntVector2> safeMove = cellStateMap.keySet().stream().filter(pos -> cellStateMap.get(pos) == Danger.SAFE).findFirst();
        if (safeMove.isPresent()) {
            logger.atInfo().log("Picking safe move %s", safeMove.get());
            return safeMove.get();
        }

        Optional<Map.Entry<IntVector2, Float>> safestOption = calculateLowestProbFromUnknowns(cellStateMap, board, numBombs);
        if (safestOption.isPresent()) {
            return safestOption.get().getKey();
        }

        // Everything touching a number looks like a bomb, there might be a square hidden in a corner
        logger.atInfo().log("unable to determine a safe option when filtering. Examining the entire board");
        Map<IntVector2, Danger> entireBoardStateMap = new HashMap<>(cellStateMap);
        MineSweeperBoardUtils.getMoves(board).forEach(pos -> entireBoardStateMap.putIfAbsent(pos, Danger.Unknown));

        // The first move hit this case
        Optional<Map.Entry<IntVector2, Float>> safestOptionFromEntireBoard = calculateLowestProbFromUnknowns(ImmutableMap.copyOf(entireBoardStateMap), board, numBombs);
        if (safestOptionFromEntireBoard.isPresent()) {
            return safestOptionFromEntireBoard.get().getKey();
        }

        Optional<IntVector2> randomNotBomb = MineSweeperBoardUtils
                .getMoves(board)
                .stream()
                .filter(pos -> Danger.BOMB.equals(cellStateMap.get(pos))).findFirst();
        if (randomNotBomb.isPresent()) {
            logger.atInfo().log("Picking a random not bomb location");
            return randomNotBomb.get();
        }

        // looks like everything is a bomb?!?
        logger.atInfo().log("Unable to make any form of a smart choice, might as well pick something random");
        return RandomUtils.randomFromList(MineSweeperBoardUtils.getMoves(board));
    }
}
