package snek.game;

import common.utils.IntVector2;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoveableSnekBodyTest {

    @Test
    void invalidBodyLength() {
        assertThrows(IllegalArgumentException.class, () -> new MoveableSnekBody(IntVector2.of(0, 0), 0));
    }

    @Test
    void moveSingleSnek() {
        MoveableSnekBody snek = new MoveableSnekBody(IntVector2.of(0, 0), 1);
        assertThat(snek.getBodyPositions()).containsExactly(IntVector2.of(0, 0));

        snek.move(Direction.UP);

        assertThat(snek.getBodyPositions()).containsExactly(IntVector2.of(0, 1));
    }

    @Test
    void moveTwoLengthSnek() {
        MoveableSnekBody snek = new MoveableSnekBody(IntVector2.of(0, 0), 2);
        assertThat(snek.getBodyPositions()).containsExactly(IntVector2.of(0, 0));

        snek.move(Direction.UP);

        assertThat(snek.getBodyPositions()).containsExactly(IntVector2.of(0, 0), IntVector2.of(0, 1));
    }

    @Test
    void moveThreeLengthSnek() {
        MoveableSnekBody snek = new MoveableSnekBody(IntVector2.of(0, 0), 3);
        assertThat(snek.getBodyPositions()).containsExactly(IntVector2.of(0, 0));

        snek.move(Direction.UP);

        assertThat(snek.getBodyPositions()).containsExactly(IntVector2.of(0, 0), IntVector2.of(0, 1));
    }

    @Test
    void extendAndMoveBodyOne() {
        MoveableSnekBody snek = new MoveableSnekBody(IntVector2.of(0, 0), 1);

        snek.extendBody(1);
        assertThat(snek.getBodyPositions()).containsExactly(IntVector2.of(0, 0));

        snek.move(Direction.UP);

        assertThat(snek.getBodyPositions()).containsExactly(IntVector2.of(0, 0), IntVector2.of(0, 1));
    }

    @Test
    void extendAndMoveBodyTwo() {
        MoveableSnekBody snek = new MoveableSnekBody(IntVector2.of(0, 0), 1);

        snek.extendBody(2);
        assertThat(snek.getBodyPositions()).containsExactly(IntVector2.of(0, 0));

        snek.move(Direction.UP);

        assertThat(snek.getBodyPositions()).containsExactly(IntVector2.of(0, 0), IntVector2.of(0, 1));
    }
}