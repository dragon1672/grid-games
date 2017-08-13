package common.board;

import com.google.common.collect.ImmutableBiMap;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoardLoadersTest {

    private static final ImmutableBiMap<Integer, Character> RGB_2_BLOCK = ImmutableBiMap.<Integer, Character>builder()
            .put(-37518, 'R')
            .put(-15814244, 'G')
            .put(-150986, 'Y')
            .build();

    @Test
    void generateFromString_simpleSquare() throws Exception {
        Board<Character> board = BoardLoaders.generateFromString("" +
                "ABC\n" +
                "DEF\n" +
                "GHI" +
                "", Function.identity());
        assertThat(board.getWidth()).isEqualTo(3);
        assertThat(board.getHeight()).isEqualTo(3);
        assertThat(board.get(0, 0)).isEqualTo('A');
        assertThat(board.get(1, 0)).isEqualTo('B');
        assertThat(board.get(2, 0)).isEqualTo('C');
        assertThat(board.get(0, 1)).isEqualTo('D');
        assertThat(board.get(1, 1)).isEqualTo('E');
        assertThat(board.get(2, 1)).isEqualTo('F');
        assertThat(board.get(0, 2)).isEqualTo('G');
        assertThat(board.get(1, 2)).isEqualTo('H');
        assertThat(board.get(2, 2)).isEqualTo('I');
    }

    @Test
    void generateFromString_simpleSquareWithTrailingLineReturn() throws Exception {
        Board<Character> board = BoardLoaders.generateFromString("" +
                "ABC\n" +
                "DEF\n" +
                "GHI\n" +
                "", Function.identity());
        assertThat(board.getWidth()).isEqualTo(3);
        assertThat(board.getHeight()).isEqualTo(3);
        assertThat(board.get(0, 0)).isEqualTo('A');
        assertThat(board.get(1, 0)).isEqualTo('B');
        assertThat(board.get(2, 0)).isEqualTo('C');
        assertThat(board.get(0, 1)).isEqualTo('D');
        assertThat(board.get(1, 1)).isEqualTo('E');
        assertThat(board.get(2, 1)).isEqualTo('F');
        assertThat(board.get(0, 2)).isEqualTo('G');
        assertThat(board.get(1, 2)).isEqualTo('H');
        assertThat(board.get(2, 2)).isEqualTo('I');
    }

    @Test
    void generateFromString_SimpleRectangle() throws Exception {
        Board<Character> board = BoardLoaders.generateFromString("" +
                "ABC\n" +
                "DEF" +
                "", Function.identity());
        assertThat(board.getWidth()).isEqualTo(3);
        assertThat(board.getHeight()).isEqualTo(2);
        assertThat(board.get(0, 0)).isEqualTo('A');
        assertThat(board.get(1, 0)).isEqualTo('B');
        assertThat(board.get(2, 0)).isEqualTo('C');
        assertThat(board.get(0, 1)).isEqualTo('D');
        assertThat(board.get(1, 1)).isEqualTo('E');
        assertThat(board.get(2, 1)).isEqualTo('F');
    }

    @Test
    void generateFromString_InvalidInput_inconsistentRows() throws Exception {
        assertThrows(IllegalArgumentException.class, () ->
            BoardLoaders.generateFromString("" +
                    "ABC\n" +
                    "DEF000\n" +
                    "HIG" +
                    "", Function.identity()));

        assertThrows(IllegalArgumentException.class, () ->
            BoardLoaders.generateFromString("" +
                    "ABC\n" +
                    "\n" +
                    "HIG" +
                    "", Function.identity()));
    }

    @Test
    void generateFromImage_square() throws Exception {
        ReadOnlyBoard<Character> board = BoardLoaders.generateFromImage("src/test/java/common/board/board_square.PNG", 3, 3, RGB_2_BLOCK::get);
        Assert.assertEquals(3, board.getWidth());
        Assert.assertEquals(3, board.getHeight());
        assertThat(board.get(0, 0)).isEqualTo('R');
        assertThat(board.get(1, 0)).isEqualTo('G');
        assertThat(board.get(2, 0)).isEqualTo('G');
        assertThat(board.get(0, 1)).isEqualTo('Y');
        assertThat(board.get(1, 1)).isEqualTo('R');
        assertThat(board.get(2, 1)).isEqualTo('G');
        assertThat(board.get(0, 2)).isEqualTo('R');
        assertThat(board.get(1, 2)).isEqualTo('R');
        assertThat(board.get(2, 2)).isEqualTo('R');
    }

    @Test
    void generateFromImage_rect() throws Exception {
        ReadOnlyBoard<Character> board = BoardLoaders.generateFromImage("src/test/java/common/board/board_rect.PNG", 3, 2, RGB_2_BLOCK::get);
        Assert.assertEquals(3, board.getWidth());
        Assert.assertEquals(2, board.getHeight());
        assertThat(board.get(0, 0)).isEqualTo('G');
        assertThat(board.get(1, 0)).isEqualTo('G');
        assertThat(board.get(2, 0)).isEqualTo('Y');
        assertThat(board.get(0, 1)).isEqualTo('R');
        assertThat(board.get(1, 1)).isEqualTo('G');
        assertThat(board.get(2, 1)).isEqualTo('Y');

    }

}