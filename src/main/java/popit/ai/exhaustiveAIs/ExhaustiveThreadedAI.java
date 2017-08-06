package popit.ai.exhaustiveAIs;

import common.board.ReadOnlyBoard;
import common.gui.BoardGui;
import common.utils.IntVector2;
import popit.ai.AI;
import popit.game.BlockColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Explores all possible outcomes and returns the best solutions
 * Right now way to slow to be usable
 */
public class ExhaustiveThreadedAI extends AI {
    private final BoardGui<BlockColor> gui;

    private final Object bestMoveLock = new Object();
    private Optional<MovePossibility> bestMove = Optional.empty();
    private CompletionService<Boolean> completionService =
            new ExecutorCompletionService<>(new ThreadPoolExecutor(6, 6,
                    0L, TimeUnit.MILLISECONDS,
                    new BlockingStack<>()));
    //new ExecutorCompletionService<>(Executors.newFixedThreadPool(6));
    private final AtomicInteger pendingTasks = new AtomicInteger();

    private void updateBestMove(Optional<MovePossibility> newMove) {
        // Double check locking ok in this instance since we are dealing with fully formed objects.
        if (bestMove.map(move -> move.score).orElse(0L) < newMove.map(move -> move.score).orElse(0L)) {
            synchronized (bestMoveLock) {
                bestMove = bestMove.map(move -> move.score).orElse(0L) < newMove.map(move -> move.score).orElse(0L) ? newMove : bestMove;
            }
        }
    }

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

    private boolean processMove(MovePossibility move) {
        long bestGameScore = bestMove.map(m -> m.score).orElse(0L);
        if (move.maxPossibleScore + move.score > bestGameScore) {
            gui.updateBoard(move.boardInstance);

            if (move.isComplete() || AI.unWinnable(move.boardInstance)) {
                updateBestMove(Optional.of(move));
            } else {
                getAllPossibleMoves(move.boardInstance).stream()
                        .map(pos -> new MovePossibility(move, pos))
                        .filter(newMove -> newMove.maxPossibleScore + newMove.score > bestGameScore)
                        .peek(newMove -> pendingTasks.incrementAndGet())
                        .forEach(newMove -> completionService.submit(() -> processMove(newMove)));
            }
        }
        pendingTasks.decrementAndGet();
        return true;
    }

    @Override
    public List<IntVector2> getMoves(ReadOnlyBoard<BlockColor> originalBoard) {
        try {
            pendingTasks.set(0);
            getAllPossibleMoves(originalBoard).stream()
                    .map(move -> new MovePossibility(originalBoard, move))
                    .forEach(this::processMove);

            while (pendingTasks.get() > 0) {
                Thread.sleep(1000);
            }
            return extractMoves(bestMove.orElseThrow(() -> new IllegalArgumentException("board not solvable")));
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
