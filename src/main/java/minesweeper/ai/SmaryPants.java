package minesweeper.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.sun.istack.internal.NotNull;
import common.board.ReadOnlyBoard;
import common.utils.Flogger;
import common.utils.IntVector2;
import common.utils.RandomUtils;
import minesweeper.game.Cell;
import minesweeper.game.MineSweeperBoardUtils;

import java.util.*;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

@SuppressWarnings("UnstableApiUsage")
public class SmaryPants implements MineSweeperAI {
    private static final Flogger logger = Flogger.getInstance();



    private static class DangerAnalysis {
        @NotNull
        public final ImmutableMap<IntVector2, Danger> dangerAnalysis;
        @NotNull
        public final ReadOnlyBoard<Cell> board;
        private final Map<Danger, Long> count_cache = new HashMap<>();

        private DangerAnalysis(@NotNull ImmutableMap<IntVector2, Danger> dangerAnalysis, @NotNull ReadOnlyBoard<Cell> board) {
            this.dangerAnalysis = dangerAnalysis;
            this.board = board;
        }

        protected long count(Danger danger) {
            return count_cache.computeIfAbsent(danger, d ->
                    dangerAnalysis.entrySet().stream().filter(entry -> entry.getValue() == danger).map(Map.Entry::getKey).count()
            );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DangerAnalysis)) return false;
            DangerAnalysis that = (DangerAnalysis) o;
            return dangerAnalysis.equals(that.dangerAnalysis) && board.equals(that.board);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dangerAnalysis, board);
        }

        public double dangerAnalysisOfPosition(IntVector2 pos, int numOfGameBombs) {
            if (dangerAnalysis.getOrDefault(pos, Danger.Unknown).equals(Danger.SAFE)) return 0;
            if (dangerAnalysis.getOrDefault(pos, Danger.Unknown).equals(Danger.BOMB)) return 1;
            // dangerAnalysis only has entries for board squares that are valid moves.
            int possibleMoves = MineSweeperBoardUtils.getMoves(board).size();
            long numOfBombs = count(Danger.BOMB);
            long numOfSafe = count(Danger.SAFE);
            long validMovesWithNoData = possibleMoves - numOfBombs - numOfSafe;
            long numOfUnknownBombs = numOfGameBombs - numOfBombs;
            return (double) numOfUnknownBombs / (double) validMovesWithNoData;
        }
    }

    private static class ExecutionTask extends DangerAnalysis {
        private ExecutionTask(ImmutableMap<IntVector2, Danger> dangerAnalysis, ReadOnlyBoard<Cell> board) {
            super(dangerAnalysis, board);
        }

        public static ExecutionTask Make(@NotNull ImmutableMap<IntVector2, Danger> knownDangers, @NotNull ReadOnlyBoard<Cell> board) {
            return new ExecutionTask(knownDangers, board);
        }

        public static Optional<ExecutionTask> Make(@NotNull IntVector2 bombAssumption, @NotNull ImmutableMap<IntVector2, Danger> knownDangers, @NotNull ReadOnlyBoard<Cell> board, int numBombs) {
            // TODO this method is doing a lot of heavy lifting...likely inappropriately.
            if (!knownDangers.get(bombAssumption).equals(Danger.Unknown)) {
                logger.atInfo().log("Bomb must be on a location that is unknown.");
                return Optional.empty();
            }

            Map<IntVector2, Danger> tmpNewAssumedDangers = new HashMap<>(knownDangers);
            tmpNewAssumedDangers.put(bombAssumption, Danger.BOMB); // override with bomb
            ImmutableMap<IntVector2, Danger> newDangers = GraphyMineSweeperAi.flagBoard(ImmutableMap.copyOf(tmpNewAssumedDangers), board);
            if (newDangers.values().stream().filter(Danger.BOMB::equals).count() > numBombs) {
                logger.atInfo().log("Too many bombs calculated, result invalid");
                return Optional.empty();
            }
            return Optional.of(new ExecutionTask(newDangers, board));
        }

        public ImmutableSet<IntVector2> getUnknowns() {
            return dangerAnalysis.entrySet().stream().filter(entry -> entry.getValue() == Danger.Unknown).map(Map.Entry::getKey).collect(toImmutableSet());
        }
    }

    /**
     * Calculate all the possible alternatives universes.
     * <p>
     * Answers the question: where could be bombs be when the board is ambiguous.
     * This will only attempt to simulate where bombs could be based off the visible numbers.
     * Making the assumption it is meaningless to determine the possible bomb locations for large chunks of undiscovered board.
     * This means the length of snapshot could be different (and less than the total number of bombs)
     */
    private static ImmutableList<DangerAnalysis> getPossibleBombLocations(ImmutableMap<IntVector2, Danger> knownDangers, ReadOnlyBoard<Cell> board, int numMines) {
        List<DangerAnalysis> values = Collections.synchronizedList(new ArrayList<>());
        values.add(ExecutionTask.Make(knownDangers, board));

        // track seen tasks to prevent dups
        Set<ExecutionTask> seenTasks = Collections.synchronizedSet(new HashSet<>());

        knownDangers
                .keySet()
                .parallelStream()
                .map(bombAssumptionPos -> ExecutionTask.Make(bombAssumptionPos, knownDangers, board, numMines))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(task -> getPossibleBombLocationsHelper(seenTasks, values, board, task, numMines));


        return ImmutableList.copyOf(values);
    }

    private static void getPossibleBombLocationsHelper(Set<ExecutionTask> seenTasks, List<DangerAnalysis> values, ReadOnlyBoard<Cell> board, ExecutionTask task, int numMines) {
        if (seenTasks.contains(task)) {
            return;
        } else {
            seenTasks.add(task);
        }
        ImmutableSet<IntVector2> unknowns = task.getUnknowns();
        if (unknowns.isEmpty()) {
            values.add(task);
        } else {
            unknowns.parallelStream()
                    .map(bombAssumptionPos -> ExecutionTask.Make(bombAssumptionPos, task.dangerAnalysis, board, numMines))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(new_task -> !seenTasks.contains(new_task))
                    .forEach(new_task -> getPossibleBombLocationsHelper(seenTasks, values, board, new_task, numMines));
        }
    }

    private static double calculateProbability(IntVector2 pos, ImmutableList<DangerAnalysis> bombAnalysisResults, int numBombs) {
        return bombAnalysisResults
                .parallelStream()
                .mapToDouble(analysis -> analysis.dangerAnalysisOfPosition(pos, numBombs))
                .average()
                .orElseThrow(() -> new IllegalArgumentException("Need some results to analyze."));
    }

    private static Map.Entry<IntVector2, Double> calculateLowestProbFromUnknowns(ImmutableMap<IntVector2, Danger> cellStateMap, ReadOnlyBoard<Cell> board, int numBombs) {
        ImmutableList<DangerAnalysis> bombAnalysisResults = getPossibleBombLocations(cellStateMap, board, numBombs);
        // Map each unknown to a probability

        // TODO optimization idea remove the obvious bombs from the first list of moves
        // TODO optimization idea group all the 'padded unknowns' which have the same probability together (special case it)
        Map<IntVector2, Double> mineProbabilities = MineSweeperBoardUtils.getMoves(board)
                .parallelStream()
                .collect(ImmutableMap.toImmutableMap(k -> k, pos -> calculateProbability(pos, bombAnalysisResults, numBombs)));
        Map.Entry<IntVector2, Double> lowestProb = mineProbabilities.entrySet().stream().min(Comparator.comparingDouble(Map.Entry::getValue)).orElseThrow(() -> new IllegalArgumentException("We gotta have some moves!"));
        logger.atInfo().log("Determined safest option of %s with a %f chance to be a bomb from %d possible states",
                lowestProb.getKey(), lowestProb.getValue(), bombAnalysisResults.size());
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

        Map.Entry<IntVector2, Double> safestOption = calculateLowestProbFromUnknowns(cellStateMap, board, numBombs);
        return safestOption.getKey();
    }
}
