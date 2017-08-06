package common.utils;

import com.google.common.collect.ImmutableSet;
import common.board.ReadOnlyBoard;
import common.board.SimulatedBoard;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BoardUtils {
    public static <T> Set<IntVector2> getConnectedCells(ReadOnlyBoard<T> board, IntVector2 startingPos) {
        Set<IntVector2> matchedSquares = new HashSet<>();
        getConnectedCells(board, startingPos.x, startingPos.y, matchedSquares);
        return Collections.unmodifiableSet(matchedSquares);
    }

    private static <T> void getConnectedCells(ReadOnlyBoard<T> board, int x, int y, Set<IntVector2> visitedSquares) {
        IntVector2 currentPos = IntVector2.of(x, y);
        if (visitedSquares.contains(currentPos)) {
            return; // we have already visited this square, ignore
        }
        visitedSquares.add(currentPos);
        if (x + 1 < board.getWidth() && board.get(x + 1, y) == board.get(x, y)) {
            getConnectedCells(board, x + 1, y, visitedSquares);
        }
        if (x - 1 >= 0 && board.get(x - 1, y) == board.get(x, y)) {
            getConnectedCells(board, x - 1, y, visitedSquares);
        }
        if (y + 1 < board.getHeight() && board.get(x, y + 1) == board.get(x, y)) {
            getConnectedCells(board, x, y + 1, visitedSquares);
        }
        if (y - 1 >= 0 && board.get(x, y - 1) == board.get(x, y)) {
            getConnectedCells(board, x, y - 1, visitedSquares);
        }
    }

    public static <T> Stream<IntVector2> boardPositionsAsStream(ReadOnlyBoard<T> board) {
        return IntStream.range(0, board.getWidth())
                .mapToObj(x -> IntStream.range(0, board.getHeight())
                        .mapToObj(y -> IntVector2.of(x, y)))
                .flatMap(stream -> stream);
    }

    public static <T> Set<IntVector2> boardPositions(ReadOnlyBoard<T> board) {
        return ImmutableSet.copyOf(boardPositionsAsStream(board).collect(Collectors.toSet()));
    }

    public static <T> boolean isUniformColor(ReadOnlyBoard<T> board) {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                if (board.get(x, y) != board.get(0, 0)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static <T> Set<T> cellsOnBoard(ReadOnlyBoard<T> board) {
        return boardPositionsAsStream(board).map(board::get).distinct().collect(Collectors.toSet());
    }

    public static <T> ReadOnlyBoard<T> replaceConnectedCells(ReadOnlyBoard<T> board, T cellToReplaceWith, IntVector2 position) {
        Set<IntVector2> connectedSquares = BoardUtils.getConnectedCells(board, position);
        Map<IntVector2, T> overrides = connectedSquares.stream()
                .collect(Collectors.toMap(pos -> pos, pos -> cellToReplaceWith));
        return new SimulatedBoard<>(board, overrides);
    }
}
