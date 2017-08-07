package common.board;

import com.google.common.collect.ImmutableBiMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.Function;

import static org.junit.Assert.fail;

public class BoardLoadersTest {

    private static final ImmutableBiMap<Integer, Character> RGB_2_BLOCK = ImmutableBiMap.<Integer, Character>builder()
            .put(-37518, 'R')
            .put(-15814244, 'G')
            .put(-150986, 'Y')
            .build();

    @Test
    public void generateFromString_simpleSquare() throws Exception {
        Board<Character> board = BoardLoaders.generateFromString("" +
                "ABC\n" +
                "DEF\n" +
                "GHI" +
                "", Function.identity());
        Assert.assertEquals(3, board.getWidth());
        Assert.assertEquals(3, board.getHeight());
        Assert.assertEquals('A', (char) board.get(0, 0));
        Assert.assertEquals('B', (char) board.get(1, 0));
        Assert.assertEquals('C', (char) board.get(2, 0));
        Assert.assertEquals('D', (char) board.get(0, 1));
        Assert.assertEquals('E', (char) board.get(1, 1));
        Assert.assertEquals('F', (char) board.get(2, 1));
        Assert.assertEquals('G', (char) board.get(0, 2));
        Assert.assertEquals('H', (char) board.get(1, 2));
        Assert.assertEquals('I', (char) board.get(2, 2));
    }

    @Test
    public void generateFromString_simpleSquareWithTrailingLineReturn() throws Exception {
        Board<Character> board = BoardLoaders.generateFromString("" +
                "ABC\n" +
                "DEF\n" +
                "GHI\n" +
                "", Function.identity());
        Assert.assertEquals(3, board.getWidth());
        Assert.assertEquals(3, board.getHeight());
        Assert.assertEquals('A', (char) board.get(0, 0));
        Assert.assertEquals('B', (char) board.get(1, 0));
        Assert.assertEquals('C', (char) board.get(2, 0));
        Assert.assertEquals('D', (char) board.get(0, 1));
        Assert.assertEquals('E', (char) board.get(1, 1));
        Assert.assertEquals('F', (char) board.get(2, 1));
        Assert.assertEquals('G', (char) board.get(0, 2));
        Assert.assertEquals('H', (char) board.get(1, 2));
        Assert.assertEquals('I', (char) board.get(2, 2));
    }

    @Test
    public void generateFromString_SimpleRectangle() throws Exception {
        Board<Character> board = BoardLoaders.generateFromString("" +
                "ABC\n" +
                "DEF" +
                "", Function.identity());
        Assert.assertEquals(3, board.getWidth());
        Assert.assertEquals(2, board.getHeight());
        Assert.assertEquals('A', (char) board.get(0, 0));
        Assert.assertEquals('B', (char) board.get(1, 0));
        Assert.assertEquals('C', (char) board.get(2, 0));
        Assert.assertEquals('D', (char) board.get(0, 1));
        Assert.assertEquals('E', (char) board.get(1, 1));
        Assert.assertEquals('F', (char) board.get(2, 1));
    }

    @Test
    public void generateFromString_InvalidInput_inconsistentRows() throws Exception {
        try {
            BoardLoaders.generateFromString("" +
                    "ABC\n" +
                    "DEF000\n" +
                    "HIG" +
                    "", Function.identity());
            fail("inconsistent rows (extra chars) expected to throw exception");
        } catch (IllegalArgumentException ignored) {
        }

        try {
            BoardLoaders.generateFromString("" +
                    "ABC\n" +
                    "\n" +
                    "HIG" +
                    "", Function.identity());
            fail("inconsistent rows (empty row) expected to throw exception");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void generateFromImage_square() throws Exception {
        ReadOnlyBoard<Character> board = BoardLoaders.generateFromImage("src/test/java/common/board/board_square.PNG", 3, 3, RGB_2_BLOCK::get);
        Assert.assertEquals(3, board.getWidth());
        Assert.assertEquals(3, board.getHeight());
        Assert.assertEquals('R', (char) board.get(0, 0));
        Assert.assertEquals('G', (char) board.get(1, 0));
        Assert.assertEquals('G', (char) board.get(2, 0));
        Assert.assertEquals('Y', (char) board.get(0, 1));
        Assert.assertEquals('R', (char) board.get(1, 1));
        Assert.assertEquals('G', (char) board.get(2, 1));
        Assert.assertEquals('R', (char) board.get(0, 2));
        Assert.assertEquals('R', (char) board.get(1, 2));
        Assert.assertEquals('R', (char) board.get(2, 2));
    }

    @Test
    public void generateFromImage_rect() throws Exception {
        ReadOnlyBoard<Character> board = BoardLoaders.generateFromImage("src/test/java/common/board/board_rect.PNG", 3, 2, RGB_2_BLOCK::get);
        Assert.assertEquals(3, board.getWidth());
        Assert.assertEquals(2, board.getHeight());
        Assert.assertEquals('G', (char) board.get(0, 0));
        Assert.assertEquals('G', (char) board.get(1, 0));
        Assert.assertEquals('Y', (char) board.get(2, 0));
        Assert.assertEquals('R', (char) board.get(0, 1));
        Assert.assertEquals('G', (char) board.get(1, 1));
        Assert.assertEquals('Y', (char) board.get(2, 1));

    }

}