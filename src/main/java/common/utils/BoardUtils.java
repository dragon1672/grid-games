package common.utils;

import common.board.ReadOnlyBoard;
import common.board.SimulatedBoard;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.collect.ImmutableSet.toImmutableSet;

public class BoardUtils {

    // for test coverage
    private BoardUtils() {
    }

    static {
        new BoardUtils();
    }

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

    public static <T> Stream<IntVector2> boardPositionsAsRandomStream(ReadOnlyBoard<T> board) {
        return RandomUtils.randomizeStream(boardPositionsAsStream(board));
    }

    public static <T> Stream<IntVector2> boardPositionsAsStream(ReadOnlyBoard<T> board) {
        return IntStream.range(0, board.getWidth())
                .mapToObj(x -> IntStream.range(0, board.getHeight())
                        .mapToObj(y -> IntVector2.of(x, y)))
                .flatMap(stream -> stream);
    }

    public static <T> Set<IntVector2> boardPositions(ReadOnlyBoard<T> board) {
        return boardPositionsAsStream(board).collect(toImmutableSet());
    }

    public static <T> boolean isUniformColor(ReadOnlyBoard<T> board) {
        checkArgument(board.isValidPos(0, 0));
        return isUniformColor(board, board.get(0, 0));
    }

    public static <T> boolean isUniformColor(ReadOnlyBoard<T> board, T color) {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                if (board.get(x, y) != color) {
                    return false;
                }
            }
        }
        return true;
    }

    public static <T> Set<T> cellTypesOnBoard(ReadOnlyBoard<T> board) {
        return boardPositionsAsStream(board).map(board::get).distinct().collect(toImmutableSet());
    }

    public static <T> ReadOnlyBoard<T> replaceConnectedCells(ReadOnlyBoard<T> board, T cellToReplaceWith, IntVector2 position) {
        Set<IntVector2> connectedSquares = BoardUtils.getConnectedCells(board, position);
        Map<IntVector2, T> overrides = connectedSquares.stream()
                .collect(toImmutableMap(pos -> pos, pos -> cellToReplaceWith));
        return new SimulatedBoard<>(board, overrides);
    }
}
