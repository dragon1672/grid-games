package minesweeper.ai;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import common.board.ReadOnlyBoard;
import common.utils.BoardUtils;
import common.utils.IntVector2;
import common.utils.RandomUtils;
import minesweeper.game.Cell;
import minesweeper.game.MineSweeper;
import minesweeper.game.MineSweeperBoardUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.ImmutableList.toImmutableList;

public class SafeBetAI implements MineSweeperAI {
    private Multimap<IntVector2, Float> generateBombProbibilities(ReadOnlyBoard<Cell> board) {
        // for each number of the board, generate probabilities for each of it's neighbors that it this number applies to it
        List<IntVector2> positionsWithNumbers = BoardUtils.boardPositionsAsStream(board)
                .filter(pos -> board.get(pos).numAdjacentBombs > 0) // only pay attention to the numbers
                .collect(toImmutableList());

        ImmutableMultimap.Builder<IntVector2, Float> results = ImmutableMultimap.builder();

        for (IntVector2 numericPosition : positionsWithNumbers) {
            List<IntVector2> neighborlyMoves = MineSweeper.DIRECTIONS.stream()
                    .map(numericPosition::add)
                    .filter(board::validPos)
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

    private int findMax(Map.Entry<IntVector2, Collection<Float>> entry1, Map.Entry<IntVector2, Collection<Float>> entry2) {
        double e1Max = entry1.getValue().stream().mapToDouble(e -> e).max().orElse(-1);
        double e2Max = entry2.getValue().stream().mapToDouble(e -> e).max().orElse(-1);
        double e1Average = entry1.getValue().stream().mapToDouble(e -> e).average().orElse(-1);
        double e2Average = entry2.getValue().stream().mapToDouble(e -> e).average().orElse(-1);

        double e1WorstCase = Math.max(e1Max, e1Average);
        double e2WorstCase = Math.max(e2Max, e2Average);

        return (int) (e1WorstCase - e2WorstCase);
    }

    @Override
    public IntVector2 getMove(ReadOnlyBoard<Cell> board) {
        Multimap<IntVector2, Float> probabilities = generateBombProbibilities(board);
        Optional<IntVector2> bestMove = probabilities.asMap().entrySet().stream()
                .max(this::findMax)
                .map(Map.Entry::getKey);
        IntVector2 randomMove = RandomUtils.randomFromList(MineSweeperBoardUtils.getMoves(board));
        return bestMove.orElse(randomMove);
    }
}
