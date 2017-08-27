package common.board;

import common.utils.IntVector2;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoardImplTest {

    @Test
    void createInvalidBoard() {
        assertThrows(IllegalArgumentException.class, () -> BoardImpl.make(-1, -1));
    }

    @Test
    void createSquareBoard() {
        Board<Character> board = BoardImpl.make(10, 10);
        assertThat(board.getWidth()).isEqualTo(10);
        assertThat(board.getHeight()).isEqualTo(10);
    }

    @Test
    void createRectangleBoard() {
        Board<Character> board = BoardImpl.make(5, 10);
        assertThat(board.getWidth()).isEqualTo(5);
        assertThat(board.getHeight()).isEqualTo(10);
    }

    @Test
    void getOutOfBounds() {
        Board<Character> board = BoardImpl.make(5, 100);
        assertThrows(IllegalArgumentException.class, () -> board.get(10, 10));
    }

    @Test
    void getOutOfBounds_negative() {
        Board<Character> board = BoardImpl.make(5, 100);
        assertThrows(IllegalArgumentException.class, () -> board.get(-1, 10));
    }

    @Test
    void checkValidPos_valid() {
        Board<Character> board = BoardImpl.make(5, 100);
        assertThat(board.isValidPos(IntVector2.of(0, 0))).isTrue();
    }

    @Test
    void checkValidPos_outOfBounds() {
        Board<Character> board = BoardImpl.make(5, 100);
        assertThat(board.isValidPos(IntVector2.of(10, 10))).isFalse();
    }

    @Test
    void checkValidPos_negative() {
        Board<Character> board = BoardImpl.make(5, 10);
        assertThat(board.isValidPos(IntVector2.of(-1, -1))).isFalse();
    }

    @Test
    void getAndSet() {
        Board<Character> board = BoardImpl.make(2, 2);
        assertThat(board.get(0, 0)).isNull();
        assertThat(board.get(1, 0)).isNull();
        assertThat(board.get(0, 1)).isNull();
        assertThat(board.get(1, 1)).isNull();

        board.set('A', IntVector2.of(0, 0));
        board.set('B', 1, 0);
        board.set('C', 0, 1);
        board.set('D', 1, 1);

        assertThat(board.get(0, 0)).isEqualTo('A');
        assertThat(board.get(1, 0)).isEqualTo('B');
        assertThat(board.get(0, 1)).isEqualTo('C');
        assertThat(board.get(1, 1)).isEqualTo('D');
    }

    @Test
    void makeCopy() {
        Board<Character> originalBoard = BoardImpl.make(1, 1);
        originalBoard.set('A', 0, 0);
        Board<Character> copy = BoardImpl.copyOf(originalBoard);
        // should be equal
        assertThat(originalBoard.get(0, 0)).isEqualTo('A');
        assertThat(copy.get(0, 0)).isEqualTo('A');

        // Assert copy doesn't change
        originalBoard.set('B', 0, 0);
        assertThat(originalBoard.get(0, 0)).isEqualTo('B');
        assertThat(copy.get(0, 0)).isEqualTo('A');

        // Assert copy doesn't mutate original
        copy.set('C', 0, 0);
        assertThat(originalBoard.get(0, 0)).isEqualTo('B');
        assertThat(copy.get(0, 0)).isEqualTo('C');
    }

    @Test
    void equals() {
        Board<Character> originalBoard = BoardImpl.make(1, 1);
        originalBoard.set('A', 0, 0);
        Board<Character> copy = BoardImpl.copyOf(originalBoard);

        assertThat(originalBoard != copy).named("object reference should be different").isTrue();
        assertThat(originalBoard).isEqualTo(copy);
        assertThat(originalBoard.hashCode()).isEqualTo(copy.hashCode());

        copy.set('B', 0, 0);
        assertThat(originalBoard).isNotEqualTo(copy);
        assertThat(originalBoard.hashCode()).isNotEqualTo(copy.hashCode());
    }

    @Test
    void equals_nullBoards() {
        Board<Character> a = BoardImpl.make(10, 10);
        Board<Character> b = BoardImpl.make(10, 10);

        assertThat(a != b).named("object reference should be different").isTrue();
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void equalsDifferentBoardSizes() {
        Board<Character> a = BoardImpl.make(1, 1);
        Board<Character> b = BoardImpl.make(2, 1);

        assertThat(a != b).named("object reference should be different").isTrue();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void testToString() {
        Board<Character> originalBoard = BoardImpl.make(1, 1);
        originalBoard.set('&', 0, 0);
        assertThat(originalBoard.toString()).contains("&");
    }
}