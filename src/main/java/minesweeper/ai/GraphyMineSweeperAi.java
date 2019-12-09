package minesweeper.ai;

import common.board.ReadOnlyBoard;
import common.utils.Flogger;
import common.utils.IntVector2;
import common.utils.RandomUtils;
import minesweeper.game.Cell;
import minesweeper.game.MineSweeper;
import minesweeper.game.MineSweeperBoardUtils;

import java.util.*;
import java.util.stream.Collectors;

public class GraphyMineSweeperAi implements MineSweeperAI {
    private static final Flogger logger = Flogger.getInstance();

    @Override
    public IntVector2 getMove(ReadOnlyBoard<Cell> board) {
        Map<IntVector2, Danger> cellStateMap = generateConnections(board);
        logger.atInfo().log("map size %d", cellStateMap.size());
        Optional<IntVector2> safeMove = cellStateMap.keySet().stream().filter(pos -> cellStateMap.get(pos) == Danger.SAFE).findFirst();
        return safeMove.orElseGet(() -> RandomUtils.randomFromList(MineSweeperBoardUtils.getMoves(board)));
    }

    private enum Danger {
        BOMB,
        Unknown,
        SAFE,
    }

    private Map<IntVector2, Danger> generateConnections(ReadOnlyBoard<Cell> board) {
        LinkedHashSet<IntVector2> squaresToCheck = new LinkedHashSet<>(MineSweeperBoardUtils.getShownNumbers(board));

        Map<IntVector2, Danger> cellStateMap = new HashMap<>();
        Optional<IntVector2> nextSquareToCheck;
        while ((nextSquareToCheck = squaresToCheck.stream().findFirst()).isPresent()) {
            IntVector2 squareToCheck = nextSquareToCheck.get();
            squaresToCheck.remove(squareToCheck);
            int touchingCount = board.get(squareToCheck).numAdjacentBombs;
            List<IntVector2> connectingMoves = MineSweeper.DIRECTIONS.stream().map(squareToCheck::add)
                    .filter(board::isValidPos)
                    .filter(pos -> board.get(pos) == Cell.EMPTY).collect(Collectors.toList());
            Set<IntVector2> changedMoves = new HashSet<>();

            // number of non safe equal bombs then all bombs
            List<IntVector2> nonSafeConnectingMoves = connectingMoves.stream().filter(pos -> cellStateMap.getOrDefault(pos, Danger.Unknown) != Danger.SAFE).collect(Collectors.toList());
            if (nonSafeConnectingMoves.size() == touchingCount) {
                for (IntVector2 pos : nonSafeConnectingMoves) {
                    if (cellStateMap.getOrDefault(pos, Danger.Unknown) != Danger.BOMB) {
                        cellStateMap.put(pos, Danger.BOMB);
                        changedMoves.add(pos);
                    }
                }
            }
            // if number of touching bombs equals number then all others safe
            List<IntVector2> touchingBombs = connectingMoves.stream()
                    .filter(pos -> cellStateMap.getOrDefault(pos, Danger.Unknown) == Danger.BOMB)
                    .collect(Collectors.toList());
            if (touchingBombs.size() == touchingCount) {
                // update all non bombs
                List<IntVector2> savePositions = connectingMoves.stream()
                        .filter(pos -> cellStateMap.getOrDefault(pos, Danger.Unknown) != Danger.BOMB)
                        .collect(Collectors.toList());
                for (IntVector2 pos : savePositions) {
                    if (cellStateMap.getOrDefault(pos, Danger.Unknown) != Danger.SAFE) {
                        cellStateMap.put(pos, Danger.SAFE);
                        changedMoves.add(pos);
                    }
                }
            }

            // any numbers touching moves that got updated should be rechecked
            changedMoves.stream()
                    .flatMap(pos -> MineSweeper.DIRECTIONS.stream().map(pos::add))
                    .filter(board::isValidPos)
                    .filter(pos -> board.get(pos).numAdjacentBombs > 0)
                    .filter(pos -> !squaresToCheck.contains(pos))
                    .forEach(squaresToCheck::add);
        }
        return cellStateMap;
    }

}
