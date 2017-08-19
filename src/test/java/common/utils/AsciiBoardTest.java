package common.utils;

import common.board.BoardLoaders;
import common.board.ReadOnlyBoard;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class AsciiBoardTest {

    @Test
    void dumbCoverageTest() {
        new AsciiBoard();
    }

    @Test
    void boardToString_singleCell() {
        String boardString = "A\n";

        ReadOnlyBoard<Character> board = BoardLoaders.generateFromString(boardString);

        assertThat(AsciiBoard.boardToString(board)).isEqualTo(boardString);
    }

    @Test
    void boardToString() {
        String boardString = "" +
                "ABC\n" +
                "DEF\n";

        ReadOnlyBoard<Character> board = BoardLoaders.generateFromString(boardString);

        assertThat(AsciiBoard.boardToString(board)).isEqualTo(boardString);
    }

}