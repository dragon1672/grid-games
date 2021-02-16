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
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

@SuppressWarnings("UnstableApiUsage")
public class SmaryPants implements MineSweeperAI {
    private static final Flogger logger = Flogger.getInstance();


    private static class ExecutionTask {
        @NotNull
        public final ImmutableMap<IntVector2, Danger> newDangers;
        @NotNull
        public final ReadOnlyBoard<Cell> board;

        private ExecutionTask(ImmutableMap<IntVector2, Danger> newDangers, ReadOnlyBoard<Cell> board) {
            this.newDangers = newDangers;
            this.board = board;
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

        public boolean hasUnknowns() {
            return newDangers.values().stream().noneMatch(Danger.Unknown::equals);
        }

        public ImmutableSet<IntVector2> getUnknowns() {
            return get(Danger.Unknown);
        }

        public ImmutableSet<IntVector2> getBombs() {
            return get(Danger.BOMB);
        }

        private ImmutableSet<IntVector2> get(Danger danger) {
            return newDangers.entrySet().stream().filter(entry -> entry.getValue() == danger).map(Map.Entry::getKey).collect(toImmutableSet());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ExecutionTask that = (ExecutionTask) o;
            return newDangers.equals(that.newDangers) && board.equals(that.board);
        }

        @Override
        public int hashCode() {
            return Objects.hash(newDangers, board);
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
    private static ImmutableList<ImmutableSet<IntVector2>> getPossibleBombLocations(ImmutableMap<IntVector2, Danger> knownDangers, ReadOnlyBoard<Cell> board, int numMines) {


        LinkedHashSet<ExecutionTask> tasks = knownDangers
                .keySet()
                .stream()
                .map(bombAssumptionPos -> ExecutionTask.Make(bombAssumptionPos, knownDangers, board, numMines))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        ImmutableList.Builder<ImmutableSet<IntVector2>> values = ImmutableList.builder();

        // track seen tasks to prevent dups (go ahead and seed with initial list of tasks)
        Set<ExecutionTask> seenTasks = new HashSet<>(tasks);

        Optional<ExecutionTask> task;
        while ((task = tasks.stream().findFirst()).isPresent()) {
            tasks.remove(task.get());
            // filtering on task consumption to simplify code
            // compared to filtering when adding to the task list
            // TODO idea of having a task execution list that remembers to consolidate this logic
            //      instead of using a LinkedHashSet
            if (seenTasks.contains(task.get())) {
                continue;
            } else {
                seenTasks.add(task.get());
            }
            if (task.get().hasUnknowns()) {
                task.get().getUnknowns().stream()
                        .map(bombAssumptionPos -> ExecutionTask.Make(bombAssumptionPos, knownDangers, board, numMines))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(tasks::add);
            } else {
                values.add(task.get().getBombs());
            }

        }
        return values.build();
    }

    private static float calculateProbability(IntVector2 pos, ImmutableList<ImmutableSet<IntVector2>> possibleBombLocations) {
        return (float) possibleBombLocations.parallelStream().filter(bombPositions -> bombPositions.contains(pos)).count() / (float) possibleBombLocations.size();
    }

    private static Optional<Map.Entry<IntVector2, Float>> calculateLowestProbFromUnknowns(ImmutableMap<IntVector2, Danger> cellStateMap, ReadOnlyBoard<Cell> board, int numBombs) {
        ImmutableList<ImmutableSet<IntVector2>> possibleBombLocations = getPossibleBombLocations(cellStateMap, board, numBombs);
        // Map each unknown to a probability

        Map<IntVector2, Float> mineProbabilities = cellStateMap.entrySet().stream()
                .filter(entry -> entry.getValue() == Danger.Unknown)
                .map(Map.Entry::getKey) // simplify just to positions now it is filtered
                .collect(ImmutableMap.toImmutableMap(k -> k, pos -> calculateProbability(pos, possibleBombLocations)));
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
