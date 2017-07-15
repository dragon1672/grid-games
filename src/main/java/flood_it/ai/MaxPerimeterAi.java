package flood_it.ai;

import common.board.ReadOnlyBoard;
import common.utils.IntVector2;
import flood_it.game.BlockColor;
import flood_it.game.BoardUtilities;
import flood_it.game.FloodIt;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AI will try and maximize perimeter with each move
 */
public class MaxPerimeterAi implements AI {

    private static int getBoardPerimeter(ReadOnlyBoard<BlockColor> board) {
        Set<IntVector2> connectedSquares = BoardUtilities.getConnectedColors(board);
        return connectedSquares
                .stream()
                .mapToInt(pos -> {
                    int sideCount = 0;
                    if (!connectedSquares.contains(pos.add(IntVector2.of(1, 0)))) {
                        sideCount++;
                    }
                    if (!connectedSquares.contains(pos.add(IntVector2.of(-1, 0)))) {
                        sideCount++;
                    }
                    if (!connectedSquares.contains(pos.add(IntVector2.of(0, 1)))) {
                        sideCount++;
                    }
                    if (!connectedSquares.contains(pos.add(IntVector2.of(0, -1)))) {
                        sideCount++;
                    }
                    return sideCount;
                })
                .sum();
    }


    @Override
    public BlockColor getMove(FloodIt game) {
        // this gets into an infinite loop where it doesn't modify the board because the moves that progress the game would decrease the service area.
        List<BlockColor> possibleMoves = BoardUtilities.movesOnBoard(game.getBoard());
        Map<BlockColor, Integer> movedAndPerimeter = possibleMoves.stream()
                .collect(Collectors.toMap(blockType -> blockType, blockType -> getBoardPerimeter(BoardUtilities.simulateFillMove(game.getBoard(), blockType))));
        Optional<Map.Entry<BlockColor, Integer>> maxEntry = movedAndPerimeter
                .entrySet()
                .stream()
                .max(Comparator.comparingInt(Map.Entry::getValue));
        return maxEntry
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalArgumentException("No possible moves"));
    }
}
