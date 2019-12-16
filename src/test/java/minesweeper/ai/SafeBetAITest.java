package minesweeper.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import common.board.BoardLoaders;
import common.board.ReadOnlyBoard;
import common.utils.IntVector2;
import minesweeper.game.Cell;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

class SafeBetAITest {

    private static char cell2Char(Cell toConvert) {
        return toConvert.toString().charAt(0);
    }

    private static Map<Character, Cell> charMap = ImmutableMap.<Character, Cell>builder()
            .put(cell2Char(Cell.N0), Cell.N0)
            .put(cell2Char(Cell.N1), Cell.N1)
            .put(cell2Char(Cell.N2), Cell.N2)
            .put(cell2Char(Cell.N3), Cell.N3)
            .put(cell2Char(Cell.N4), Cell.N4)
            .put(cell2Char(Cell.N5), Cell.N5)
            .put(cell2Char(Cell.N6), Cell.N6)
            .put(cell2Char(Cell.N7), Cell.N7)
            .put(cell2Char(Cell.N8), Cell.N8)
            .put(cell2Char(Cell.BOMB), Cell.EMPTY)
            .put(cell2Char(Cell.EMPTY), Cell.EMPTY)
            .build();

    private ReadOnlyBoard<Cell> getBoard(String boardStr) {
        return BoardLoaders.generateFromString(boardStr, charMap::get);
    }

    @Test
    void getBoardTranslatesBombsToEmpty() {
        assertThat(getBoard("EBE\n").toString()).isEqualTo("EEE\n");
    }

    @Test
    void getMove_firstMove() {
        ReadOnlyBoard<Cell> board = getBoard("" +
                "EE\n" +
                "EE\n" +
                "");

        IntVector2 result = new SafeBetAI().getMove(board, 1);

        assertThat(result).isAnyOf(
                IntVector2.of(0, 0),
                IntVector2.of(0, 1),
                IntVector2.of(1, 0),
                IntVector2.of(1, 1));
    }

    @Test
    void getMove_singleCell() {
        ReadOnlyBoard<Cell> board = getBoard("" +
                "E\n" +
                "");

        List<IntVector2> expected = ImmutableList.of(IntVector2.of(0, 0));

        List<IntVector2> result = new SafeBetAI().getMoves(board);

        assertThat(result).containsExactlyElementsIn(expected);
    }

    @Test
    void getMove_singleValidMove() {
        ReadOnlyBoard<Cell> board = getBoard("" +
                "000\n" +
                "0E0\n" +
                "000\n" +
                "");

        List<IntVector2> expected = ImmutableList.of(IntVector2.of(1, 1));

        List<IntVector2> result = new SafeBetAI().getMoves(board);

        assertThat(result).containsExactlyElementsIn(expected);
    }

    @Test
    void getMove_2Bombs() {
        ReadOnlyBoard<Cell> board = getBoard("" +
                "111111\n" +
                "1B1EB1\n" +
                "111111\n" +
                "");

        List<IntVector2> expected = ImmutableList.of(IntVector2.of(3, 1));

        List<IntVector2> result = new SafeBetAI().getMoves(board);

        assertThat(result).containsExactlyElementsIn(expected);
    }

    @Test
    void getMove_2BombsBug() {
        ReadOnlyBoard<Cell> board = getBoard("" +
                "11111\n" +
                "1B1EB\n" +
                "11111\n" +
                "");

        List<IntVector2> expected = ImmutableList.of(IntVector2.of(3, 1));

        List<IntVector2> result = new SafeBetAI().getMoves(board);

        assertThat(result).containsExactlyElementsIn(expected);
    }

    @Test
    void getMove_largerNumbers() {
        ReadOnlyBoard<Cell> board = getBoard("" +
                "11211\n" +
                "1B2BE\n" +
                "11211\n" +
                "");

        List<IntVector2> expected = ImmutableList.of(IntVector2.of(4, 1));

        List<IntVector2> result = new SafeBetAI().getMoves(board);

        assertThat(result).containsExactlyElementsIn(expected);
    }
}