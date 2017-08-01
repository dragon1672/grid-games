package popit.ai.exhaustiveAIs;

import com.google.common.collect.ImmutableList;
import common.board.ReadOnlyBoard;
import common.gui.BoardGui;
import common.utils.IntVector2;
import popit.ai.AI;
import popit.game.BlockColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Explores all possible outcomes and returns the best solutions
 * Right now way to slow to be usable
 */
public class ExhaustiveThreadedAI extends AI {
    private final BoardGui<BlockColor> gui;

    public ExhaustiveThreadedAI(BoardGui<BlockColor> gui) {
        this.gui = gui;
    }

    private static List<IntVector2> extractMoves(MovePossibility movePossibility) {
        List<IntVector2> moves = new ArrayList<>();
        extractMoves(movePossibility, moves);
        return moves;
    }

    private static void extractMoves(MovePossibility movePossibility, List<IntVector2> moves) {
        if (movePossibility.parent != null) {
            extractMoves(movePossibility.parent, moves);
        }
        moves.add(movePossibility.moveToMake);
    }

    private TaskResult processMove(MovePossibility move, Optional<MovePossibility> currentBestMove) {
        gui.updateBoard(move.boardInstance);

        long bestGameScore = currentBestMove.map(m -> m.score).orElse(0L);

        ImmutableList.Builder<MovePossibility> generatedMoves = ImmutableList.builder();

        Optional<MovePossibility> bestGame = Optional.empty();

        if (move.isComplete() || AI.unWinnable(move.boardInstance)) {
            if (move.isComplete() && move.score > bestGameScore) {
                bestGame = Optional.of(move);
            }
        } else {
            getAllPossibleMoves(move.boardInstance).stream()
                    .map(pos -> new MovePossibility(move, pos))
                    .filter(newMove -> newMove.maxPossibleScore + newMove.score > bestGameScore)
                    .forEach(generatedMoves::add);
        }

        return new TaskResult(bestGame, generatedMoves.build());
    }

    @Override
    public List<IntVector2> getMoves(ReadOnlyBoard<BlockColor> originalBoard) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        List<Future<TaskResult>> pendingTasks = new ArrayList<>();

        Optional<MovePossibility> bestMove = Optional.empty();

        pendingTasks.add(executorService.submit(() -> new TaskResult(Optional.empty(),
                getAllPossibleMoves(originalBoard).stream()
                        .map(move -> new MovePossibility(originalBoard, move))
                        .collect(Collectors.toList())
        )));

        while (!pendingTasks.isEmpty()) {
            //noinspection ResultOfMethodCallIgnored
            pendingTasks.parallelStream().map(this::getFuture).findFirst(); // wait till something is resolved

            List<Future<TaskResult>> completedTasks = pendingTasks.stream().filter(Future::isDone).collect(Collectors.toList());
            pendingTasks.removeAll(completedTasks);

            bestMove = getBestMove(bestMove, completedTasks.stream().map(this::getFuture).map(task -> task.bestMove).reduce(ExhaustiveThreadedAI::getBestMove).orElse(Optional.empty()));

            try {
                Optional<MovePossibility> currentBestMove = bestMove;
                for (Future<TaskResult> taskResult : completedTasks) {
                    pendingTasks.addAll(executorService.invokeAll(taskResult.get().newMoves.stream()
                            .map(move -> (Callable<TaskResult>) () -> processMove(move, currentBestMove))
                            .collect(Collectors.toList())));
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return extractMoves(bestMove.orElseThrow(() -> new IllegalArgumentException("board not solveable")));
    }

    private static Optional<MovePossibility> getBestMove(Optional<MovePossibility> a, Optional<MovePossibility> b) {
        return a.map(move -> move.score).orElse(0L) > b.map(move -> move.score).orElse(0L) ? a : b;
    }

    private <T> T getFuture(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static class TaskResult {
        final Optional<MovePossibility> bestMove;
        final List<MovePossibility> newMoves;

        private TaskResult(Optional<MovePossibility> bestMove, List<MovePossibility> newMoves) {
            this.bestMove = bestMove;
            this.newMoves = newMoves;
        }

    }
}
