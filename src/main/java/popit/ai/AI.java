package popit.ai;


import common.board.ReadOnlyBoard;
import common.utils.BoardUtils;
import common.utils.IntVector2;
import popit.game.BlockColor;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * My Dumb AIs for the breaker game
 */
public abstract class AI implements PopItAi {
    // TODO: Add more checks
    // These checks
    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    private static final List<Predicate<ReadOnlyBoard<BlockColor>>> failIfTrueChecks = Arrays.asList(
            // Check if not enough colors left to win
            board -> BoardUtils.boardPositionsAsStream(board)
                    .map(board::get)
                    .filter(color -> color != BlockColor.WHITES_INVALID)
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).values()
                    .stream()
                    .anyMatch(value -> value == 1)
    );
    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    private static final List<Predicate<ReadOnlyBoard>> failIfFalseChecks = Arrays.asList();

    /**
     * Returns true if the current board is unable to win
     * This will not modify the board
     *
     * @param board const board to check
     * @return true if current board cannot win, false if unknown
     */
    static boolean unWinnable(ReadOnlyBoard<BlockColor> board) {
        if (failIfTrueChecks.stream().anyMatch(checker -> checker.test(board))) {
            return true;
        }
        if (failIfFalseChecks.stream().anyMatch(checker -> !checker.test(board))) {
            return true;
        }
        // Unknown
        return false;
    }

    /**
     * Gets the shortest list of all unique moves.  For each given group only 1 of their positions will exist in this set
     *
     * @param board board to evaluate
     * @return shortest list of all unique moves
     */
    static Set<IntVector2> getAllPossibleMoves(ReadOnlyBoard<BlockColor> board) {
        return getAllPossibleMovesMap(board).keySet();
    }

    /**
     * Gets the shortest list of all unique moves and their scores.  For each given group only 1 of their positions will exist in this set
     *
     * @param board board to check
     * @return shortest list of all unique moves and their scores
     */
    static Map<IntVector2, Integer> getAllPossibleMovesMap(ReadOnlyBoard<BlockColor> board) {
        Set<IntVector2> consideredPositions = new HashSet<>();
        Map<IntVector2, Integer> possibleMoves = new HashMap<>();
        BoardUtils.boardPositionsAsStream(board)
                .filter(pos -> board.get(pos) != BlockColor.WHITES_INVALID)
                .forEach(pos -> {
                    if (!consideredPositions.contains(pos)) {
                        Set<IntVector2> matchResult = BoardUtils.getConnectedCells(board, pos);
                        if (matchResult.size() > 1) {
                            possibleMoves.put(pos, matchResult.size());
                            consideredPositions.addAll(matchResult);
                        }
                    }
                });
        return possibleMoves;
    }
}
