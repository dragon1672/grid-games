package game2048.game;

import com.google.common.collect.ImmutableList;
import common.board.Board;
import common.board.BoardLoaders;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

class Utils2048Test {

    private static Board<Cell> genBoard(String boardStr) {
        return BoardLoaders.generateFromString(boardStr, ch -> Cell.cellNumMap.inverse().get(ch - '0'));
    }

    DynamicTest testMergeCell(String input, Move move, String expected, String name) {
        return DynamicTest.dynamicTest(
                name,
                () -> {
                    Board<Cell> board = genBoard(input);

                    Utils2048.mergeCells(board, move);

                    assertThat(board).named("original board: " + input).isEqualTo(genBoard(expected));
                });
    }

    @TestFactory
    List<DynamicTest> mergeCells() {
        return ImmutableList.of(
                testMergeCell("101", Move.RIGHT, "002", "with space"),
                testMergeCell("011", Move.RIGHT, "002", "touching"),
                testMergeCell("111", Move.RIGHT, "012", "3 in a row"),
                testMergeCell("112", Move.RIGHT, "022", "don't double merge"),
                testMergeCell("123", Move.RIGHT, "123", "no merges"),
                testMergeCell("000", Move.RIGHT, "000", "all 0")
        );
    }
}