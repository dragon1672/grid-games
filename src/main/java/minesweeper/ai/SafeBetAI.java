package minesweeper.ai;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import common.board.ReadOnlyBoard;
import common.utils.IntVector2;
import common.utils.RandomUtils;
import minesweeper.game.Cell;
import minesweeper.game.MineSweeper;
import minesweeper.game.MineSweeperBoardUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableList.toImmutableList;

public class SafeBetAI implements MineSweeperAI {
    private Multimap<IntVector2, Float> generateBombProbabilities(ReadOnlyBoard<Cell> board) {
        // for each number of the board, generate probabilities for each of it's neighbors that it this number applies to it
        List<IntVector2> positionsWithNumbers = MineSweeperBoardUtils.getShownNumbers(board);

        ImmutableMultimap.Builder<IntVector2, Float> results = ImmutableMultimap.builder();

        for (IntVector2 numericPosition : positionsWithNumbers) {
            List<IntVector2> neighborlyMoves = MineSweeper.DIRECTIONS.stream()
                    .map(numericPosition::add)
                    .filter(board::isValidPos)
                    .filter(pos -> board.get(pos) == Cell.EMPTY)
                    .collect(toImmutableList());
            // take the number and divide it by the number of open squares adjacent to it
            // If a number of only has 1 option next to it, this will be 1/1 = 1
            // if there are 2 open cells next to a 1, this will be 1/2 = .5
            float perCellProbability = (float) board.get(numericPosition).numAdjacentBombs / neighborlyMoves.size();
            neighborlyMoves.forEach(pos -> results.put(pos, perCellProbability));
        }

        return results.build();
    }

    private double findMax(Map.Entry<IntVector2, Collection<Float>> entry) {
        double e1Max = entry.getValue().stream().mapToDouble(e -> e).max().orElse(-1);
        double e1Average = entry.getValue().stream().mapToDouble(e -> e).average().orElse(-1);

        return Math.max(e1Max, e1Average);
    }

    @VisibleForTesting
    List<IntVector2> getMoves(ReadOnlyBoard<Cell> board) {
        Multimap<IntVector2, Float> probabilities = generateBombProbabilities(board);
        Optional<List<IntVector2>> bestMoves = probabilities.asMap().entrySet().stream()
                // group by heuristic and discard the collection of probabilities
                .collect(Collectors.groupingBy(this::findMax, Collectors.mapping(Map.Entry::getKey, toImmutableList())))
                .entrySet()
                .stream()
                .min(Comparator.comparingDouble(Map.Entry::getKey))
                .map(Map.Entry::getValue);
        return bestMoves.orElse(MineSweeperBoardUtils.getMoves(board));
    }

    @Override
    public IntVector2 getMove(ReadOnlyBoard<Cell> board, int numBombs) {
        return RandomUtils.randomFromList(getMoves(board));
    }
}
