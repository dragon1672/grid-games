package common.board;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.fail;

public class BoardImplTest {

    @Test
    public void createInvalidBoard() {
        try {
            BoardImpl.make(-1, -1);
            fail("negative width/height expected to fail");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void createSquareBoard() {
        Board<Character> board = BoardImpl.make(10, 10);
        Assert.assertEquals(10, board.getWidth());
        Assert.assertEquals(10, board.getHeight());
    }

    @Test
    public void createRectangleBoard() {
        Board<Character> board = BoardImpl.make(5, 10);
        Assert.assertEquals(5, board.getWidth());
        Assert.assertEquals(10, board.getHeight());
    }

    @Test
    public void getOutOfBounds() {
        Board<Character> board = BoardImpl.make(5, 10);
        try {
            board.get(10, 10);
            fail("x of 10 should be out of bounds");
        } catch (IllegalArgumentException ignored) {
        }

        try {
            board.get(-1, 10);
            fail("x of -1 should be out of bounds");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void getOutOfBounds_negative() {
        Board<Character> board = BoardImpl.make(5, 10);
        try {
            board.get(-1, 10);
            fail("x of -1 should be out of bounds");
        } catch (IllegalArgumentException ignored) {
        }
    }


    @Test
    public void getAndSet() {
        Board<Character> board = BoardImpl.make(2, 2);
        Assert.assertNull(board.get(0, 0));
        Assert.assertNull(board.get(1, 0));
        Assert.assertNull(board.get(0, 1));
        Assert.assertNull(board.get(1, 1));

        board.set('A', 0, 0);
        board.set('B', 1, 0);
        board.set('C', 0, 1);
        board.set('D', 1, 1);

        Assert.assertEquals('A', (char) board.get(0, 0));
        Assert.assertEquals('B', (char) board.get(1, 0));
        Assert.assertEquals('C', (char) board.get(0, 1));
        Assert.assertEquals('D', (char) board.get(1, 1));
    }

    @Test
    public void makeCopy() {
        Board<Character> originalBoard = BoardImpl.make(1, 1);
        originalBoard.set('A', 0, 0);
        Board<Character> copy = BoardImpl.copyOf(originalBoard);
        // should be equal
        Assert.assertEquals('A', (char) originalBoard.get(0, 0));
        Assert.assertEquals('A', (char) copy.get(0, 0));

        // Assert copy doesn't change
        originalBoard.set('B', 0, 0);
        Assert.assertEquals('B', (char) originalBoard.get(0, 0));
        Assert.assertEquals('A', (char) copy.get(0, 0));

        // Assert copy doesn't mutate original
        copy.set('C', 0, 0);
        Assert.assertEquals('B', (char) originalBoard.get(0, 0));
        Assert.assertEquals('C', (char) copy.get(0, 0));
    }

    @Test
    public void equals() {
        Board<Character> originalBoard = BoardImpl.make(1, 1);
        originalBoard.set('A', 0, 0);
        Board<Character> copy = BoardImpl.copyOf(originalBoard);

        Assert.assertTrue("object reference should be different", originalBoard != copy);
        Assert.assertEquals(originalBoard, copy);
        Assert.assertEquals(originalBoard.hashCode(), copy.hashCode());

        copy.set('B', 0, 0);
        Assert.assertNotEquals(originalBoard, copy);
        Assert.assertNotEquals(originalBoard.hashCode(), copy.hashCode());
    }

    @Test
    public void testToString() {
        Board<Character> originalBoard = BoardImpl.make(1, 1);
        originalBoard.set('&', 0, 0);
        Assert.assertTrue("expected to contain char '&'", originalBoard.toString().contains("&"));
    }
}