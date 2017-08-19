package flood_it.ai;

import common.board.ReadOnlyBoard;
import common.utils.BoardUtils;
import common.utils.IntVector2;
import flood_it.game.FloodColor;
import flood_it.game.FloodIt;
import flood_it.game.FloodItBoardUtilities;

import java.util.*;
import java.util.stream.Collectors;

/**
 * FlooditAi will try and maximize perimeter with each move
 */
public class MaxPerimeterFlooditAi implements FlooditAI {

    private static int getBoardPerimeter(ReadOnlyBoard<FloodColor> board) {
        Set<IntVector2> connectedSquares = BoardUtils.getConnectedCells(board, FloodIt.MOVE_POS);
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
    public FloodColor getMove(FloodIt game) {
        // this gets into an infinite loop where it doesn't modify the board because the moves that progress the game would decrease the service area.
        List<FloodColor> possibleMoves = FloodItBoardUtilities.movesOnBoard(game.getBoard());
        Map<FloodColor, Integer> movedAndPerimeter = possibleMoves.stream()
                .collect(Collectors.toMap(blockType -> blockType, blockType -> getBoardPerimeter(FloodItBoardUtilities.simulateFillMove(game.getBoard(), blockType))));
        Optional<Map.Entry<FloodColor, Integer>> maxEntry = movedAndPerimeter
                .entrySet()
                .stream()
                .max(Comparator.comparingInt(Map.Entry::getValue));
        return maxEntry
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalArgumentException("No possible moves"));
    }
}
