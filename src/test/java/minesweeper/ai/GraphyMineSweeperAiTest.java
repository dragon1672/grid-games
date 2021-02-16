package minesweeper.ai;

import common.board.BoardLoaders;
import common.board.ReadOnlyBoard;
import common.utils.IntVector2;
import minesweeper.game.Cell;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

class GraphyMineSweeperAiTest {

    @Test
    void flagBoard() {

        ReadOnlyBoard<Cell> board = boardFromString("" +
                "111\n" +
                "1??\n" +
                "");

        Map<IntVector2, Danger> flags = GraphyMineSweeperAi.flagBoard(board);

        assertThat(flags).containsEntry(IntVector2.of(1, 1), Danger.BOMB);
        assertThat(flags).containsEntry(IntVector2.of(2, 1), Danger.SAFE);
    }

    private ReadOnlyBoard<Cell> boardFromString(String str) {
        return BoardLoaders.generateFromString(str, this::fromChar);
    }

    public Cell fromChar(char c) {
        switch (c) {
            case ' ':
                return Cell.N0;
            case '1':
                return Cell.N1;
            case '2':
                return Cell.N2;
            case '3':
                return Cell.N3;
            case '4':
                return Cell.N4;
            case '5':
                return Cell.N5;
            case '6':
                return Cell.N6;
            case '7':
                return Cell.N7;
            case '8':
                return Cell.N8;
            case 'X':
                return Cell.BOMB;
            case '?':
                return Cell.EMPTY;
            default:
                return null;
        }
    }
}