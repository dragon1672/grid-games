package minesweeper.game;

import common.utils.IntVector2;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MineSweeperTest {

    @Test
    void creatingABoardStartEmpty() {
        MineSweeper game = MineSweeper.createFromString("" +
                "  \n" +
                " X\n" +
                "  \n" +
                "", 'X');
        assertThat(game.getBoard().getWidth()).isEqualTo(2);
        assertThat(game.getBoard().getHeight()).isEqualTo(3);
        assertThat(game.getBoard().get(0, 0)).isEqualTo(Cell.EMPTY);
        assertThat(game.getBoard().get(1, 0)).isEqualTo(Cell.EMPTY);
        assertThat(game.getBoard().get(0, 1)).isEqualTo(Cell.EMPTY);
        assertThat(game.getBoard().get(1, 1)).isEqualTo(Cell.EMPTY);
        assertThat(game.getBoard().get(0, 2)).isEqualTo(Cell.EMPTY);
        assertThat(game.getBoard().get(1, 2)).isEqualTo(Cell.EMPTY);
    }

    @Test
    void clickingBombEndsGame() {
        MineSweeper game = MineSweeper.createFromString("" +
                "  \n" +
                " X\n" +
                "", 'X');
        assertThat(game.isComplete()).isFalse();
        assertThat(game.hasLost()).isFalse();
        assertThat(game.hasWon()).isFalse();

        game.move(IntVector2.of(1, 1));

        assertThat(game.isComplete()).isTrue();
        assertThat(game.hasLost()).isTrue();
        assertThat(game.hasWon()).isFalse();
    }

    @Test
    void clickingEverythingElseEndsGame() {
        MineSweeper game = MineSweeper.createFromString("" +
                "  \n" +
                " X\n" +
                "", 'X');
        assertThat(game.isComplete()).isFalse();
        assertThat(game.hasLost()).isFalse();
        assertThat(game.hasWon()).isFalse();

        game.move(IntVector2.of(0, 0));
        game.move(IntVector2.of(0, 1));
        game.move(IntVector2.of(1, 0));

        assertThat(game.isComplete()).isTrue();
        assertThat(game.hasLost()).isFalse();
        assertThat(game.hasWon()).isTrue();
    }

    @Test
    void calculatesCellValueOne() {
        MineSweeper game = MineSweeper.createFromString("" +
                "X \n" +
                "", 'X');

        game.move(IntVector2.of(1, 0));

        assertThat(game.getBoard().get(1, 0)).isEqualTo(Cell.N1);
    }

    @Test
    void calculatesCellValueTwo() {
        MineSweeper game = MineSweeper.createFromString("" +
                "X X\n" +
                "", 'X');

        game.move(IntVector2.of(1, 0));

        assertThat(game.getBoard().get(1, 0)).isEqualTo(Cell.N2);
    }

    @Test
    void calculatesCellValueThree() {
        MineSweeper game = MineSweeper.createFromString("" +
                "X  \n" +
                "X X\n" +
                "", 'X');

        game.move(IntVector2.of(1, 1));

        assertThat(game.getBoard().get(1, 1)).isEqualTo(Cell.N3);
    }

    @Test
    void calculatesCellValueFour() {
        MineSweeper game = MineSweeper.createFromString("" +
                "XX \n" +
                "X X\n" +
                "", 'X');

        game.move(IntVector2.of(1, 1));

        assertThat(game.getBoard().get(1, 1)).isEqualTo(Cell.N4);
    }

    @Test
    void calculatesCellValueFive() {
        MineSweeper game = MineSweeper.createFromString("" +
                "XXX\n" +
                "X X\n" +
                "", 'X');

        game.move(IntVector2.of(1, 1));

        assertThat(game.getBoard().get(1, 1)).isEqualTo(Cell.N5);
    }

    @Test
    void calculatesCellValueSix() {
        MineSweeper game = MineSweeper.createFromString("" +
                "X  \n" +
                "X X\n" +
                "XXX\n" +
                "", 'X');

        game.move(IntVector2.of(1, 1));

        assertThat(game.getBoard().get(1, 1)).isEqualTo(Cell.N6);
    }

    @Test
    void calculatesCellValueSeven() {
        MineSweeper game = MineSweeper.createFromString("" +
                "XX \n" +
                "X X\n" +
                "XXX\n" +
                "", 'X');

        game.move(IntVector2.of(1, 1));

        assertThat(game.getBoard().get(1, 1)).isEqualTo(Cell.N7);
    }

    @Test
    void calculatesCellValueEight() {
        MineSweeper game = MineSweeper.createFromString("" +
                "XXX\n" +
                "X X\n" +
                "XXX\n" +
                "", 'X');

        game.move(IntVector2.of(1, 1));

        assertThat(game.getBoard().get(1, 1)).isEqualTo(Cell.N8);
    }

    @Test
    void onlyTouchingBombCount() {
        MineSweeper game = MineSweeper.createFromString("" +
                "X  X\n" +
                "", 'X');

        game.move(IntVector2.of(1, 0));
        assertThat(game.getBoard().get(1, 0)).isEqualTo(Cell.N1);

        game.move(IntVector2.of(2, 0));
        assertThat(game.getBoard().get(2, 0)).isEqualTo(Cell.N1);
    }

    @Test
    void generateBoardWithTooManyBombs() {
        //Bombs cannot be placed in a circle around the first click
        assertThrows(IllegalArgumentException.class, () -> MineSweeper.create(2, 2, 1));

        assertThrows(IllegalArgumentException.class, () -> MineSweeper.create(10, 10, 100));
    }

    @Test
    void generateBoardWithNoBombs() {
        assertThrows(IllegalArgumentException.class, () -> MineSweeper.create(10, 10, 0));
    }

    @Test
    void firstClickDoesntFail() {
        MineSweeper game = MineSweeper.create(10, 10, 91);
        game.move(IntVector2.of(1, 1));
        assertThat(game.hasLost()).isFalse();
    }

    @Test
    void firstClickSurroundedByEmpty() {
        MineSweeper game = MineSweeper.create(10, 10, 91);
        game.move(IntVector2.of(1, 1));
        assertThat(game.getBoard().get(1, 1)).isEqualTo(Cell.N0);
    }

    @Test
    void emptySpotsRevealSurrounding() {
        MineSweeper game = MineSweeper.createFromString("" +
                "XXXXX\n" +
                "X535X\n" +
                "X303X\n" +
                "X535X\n" +
                "XXXXX\n" +
                "", 'X');
        game.move(IntVector2.of(2, 2));
        assertThat(game.getBoard().get(2, 2)).isEqualTo(Cell.N0);
        assertThat(game.getBoard().get(1, 1)).isEqualTo(Cell.N5);
    }

    @Test
    void emptySpotsRevealSurroundingExtended() {
        MineSweeper game = MineSweeper.createFromString("" +
                "XXXXXXXX\n" +
                "X533335X\n" +
                "X300003X\n" +
                "X533335X\n" +
                "XXXXXXXX\n" +
                "", 'X');
        game.move(IntVector2.of(2, 2));
        assertThat(game.getBoard().get(2, 2)).isEqualTo(Cell.N0);
        assertThat(game.getBoard().get(3, 2)).isEqualTo(Cell.N0);
        assertThat(game.getBoard().get(4, 2)).isEqualTo(Cell.N0);
        assertThat(game.getBoard().get(5, 2)).isEqualTo(Cell.N0);
        assertThat(game.getBoard().get(6, 2)).isEqualTo(Cell.N3);
        assertThat(game.getBoard().get(1, 1)).isEqualTo(Cell.N5);
    }
}