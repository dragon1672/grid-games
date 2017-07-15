package common.utils;


import common.board.ReadOnlyBoard;

import java.util.function.Function;

/**
 * Helper to convert block types
 */
public class AsciiBoard {
    public static <T> String boardToString(ReadOnlyBoard<T> gameBoard) {
        return boardToString(gameBoard, cell -> cell.toString().charAt(0));
    }

    public static <T> String boardToString(ReadOnlyBoard<T> gameBoard, Function<T, Character> cellToChar) {
        StringBuilder sb = new StringBuilder((gameBoard.getWidth() + 1) * gameBoard.getHeight());
        for (int y = 0; y < gameBoard.getHeight(); y++) {
            for (int x = 0; x < gameBoard.getWidth(); x++) {
                sb.append(cellToChar.apply(gameBoard.get(x, y)));
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
