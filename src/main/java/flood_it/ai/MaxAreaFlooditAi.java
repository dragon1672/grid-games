package flood_it.ai;

import flood_it.game.BlockColor;
import flood_it.game.BoardUtilities;
import flood_it.game.FloodIt;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MineSweeperAI will try take any move that will convert the most blocks
 */
public class MaxAreaFlooditAi implements FlooditAI {

    @Override
    public BlockColor getMove(FloodIt game) {

        List<BlockColor> possibleMoves = BoardUtilities.movesOnBoard(game.getBoard());

        Map<BlockColor, Integer> movedAndPerimeter = possibleMoves.stream()
                .collect(Collectors.toMap(blockType -> blockType, blockType -> BoardUtilities.getConnectedColors(BoardUtilities.simulateFillMove(game.getBoard(), blockType)).size()));

        Optional<Map.Entry<BlockColor, Integer>> maxEntry = movedAndPerimeter
                .entrySet()
                .stream()
                .max(Comparator.comparingInt(Map.Entry::getValue));

        return maxEntry
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalArgumentException("No possible moves"));
    }
}
