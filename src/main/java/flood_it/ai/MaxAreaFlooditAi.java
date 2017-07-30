package flood_it.ai;

import common.utils.BoardUtils;
import flood_it.game.FloodColor;
import flood_it.game.FloodIt;
import flood_it.game.FloodItBoardUtilities;

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
    public FloodColor getMove(FloodIt game) {

        List<FloodColor> possibleMoves = FloodItBoardUtilities.movesOnBoard(game.getBoard());

        Map<FloodColor, Integer> movedAndPerimeter = possibleMoves.stream()
                .collect(Collectors.toMap(blockType -> blockType, blockType -> BoardUtils.getConnectedCells(FloodItBoardUtilities.simulateFillMove(game.getBoard(), blockType), FloodIt.MOVE_POS).size()));

        Optional<Map.Entry<FloodColor, Integer>> maxEntry = movedAndPerimeter
                .entrySet()
                .stream()
                .max(Comparator.comparingInt(Map.Entry::getValue));

        return maxEntry
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalArgumentException("No possible moves"));
    }
}
