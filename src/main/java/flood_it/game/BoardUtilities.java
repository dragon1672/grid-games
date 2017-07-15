package flood_it.game;

import com.google.common.collect.ImmutableSet;
import common.board.ReadOnlyBoard;
import common.board.SimulatedBoard;
import common.utils.IntVector2;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Helpful Generic Board Utilities
 */
public class BoardUtilities {
    public static Set<IntVector2> getConnectedColors(ReadOnlyBoard<BlockColor> board) {
        Set<IntVector2> matchedSquares = new HashSet<>();
        getConnectedColors(board, 0, 0, matchedSquares);
        return Collections.unmodifiableSet(matchedSquares);
    }

    private static void getConnectedColors(ReadOnlyBoard<BlockColor> board, int x, int y, Set<IntVector2> visitedSquares) {
        IntVector2 currentPos = IntVector2.of(x, y);
        if (visitedSquares.contains(currentPos)) {
            return; // we have already visited this square, ignore
        }
        visitedSquares.add(currentPos);
        if (x + 1 < board.getWidth() && board.get(x + 1, y) == board.get(x, y)) {
            getConnectedColors(board, x + 1, y, visitedSquares);
        }
        if (x - 1 >= 0 && board.get(x - 1, y) == board.get(x, y)) {
            getConnectedColors(board, x - 1, y, visitedSquares);
        }
        if (y + 1 < board.getHeight() && board.get(x, y + 1) == board.get(x, y)) {
            getConnectedColors(board, x, y + 1, visitedSquares);
        }
        if (y - 1 >= 0 && board.get(x, y - 1) == board.get(x, y)) {
            getConnectedColors(board, x, y - 1, visitedSquares);
        }
    }

    private static Set<BlockColor> colorsOnBoard(ReadOnlyBoard<BlockColor> board) {
        //*
        ImmutableSet.Builder<BlockColor> colors = ImmutableSet.builder();
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                colors.add(board.get(x, y));
            }
        }
        return colors.build();
        /*/
        return IntStream.range(0,board.getWidth())
                .mapToObj(x -> IntStream.range(0,board.getHeight())
                        .mapToObj(y -> board.get(x,y)))
                .flatMap(y -> y)
                .distinct()
                .collect(Collectors.toSet());
        //*/
    }

    public static List<BlockColor> movesOnBoard(ReadOnlyBoard<BlockColor> board) {
        return Collections.unmodifiableList(colorsOnBoard(board).stream().filter(cell -> cell != board.get(0, 0)).collect(Collectors.toList()));
    }

    static boolean isUniformColor(ReadOnlyBoard<BlockColor> board) {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                if (board.get(x, y) != board.get(0, 0)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static SimulatedBoard<BlockColor> simulateFillMove(ReadOnlyBoard<BlockColor> board, BlockColor newBlockColor) {
        Set<IntVector2> connectedSquares = getConnectedColors(board);
        Map<IntVector2, BlockColor> overrides = connectedSquares.stream()
                .collect(Collectors.toMap(pos -> pos, pos -> newBlockColor));
        return new SimulatedBoard<>(board, overrides);
    }
}
