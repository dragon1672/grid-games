package conway_lyfe.game;

import com.google.common.collect.ImmutableList;
import common.utils.IntVector2;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class ConwayBoardTest {

    @Test
    void BuilderActiveTrue() {
        ConwayBoard.Builder builder = ConwayBoard.builder();
        IntVector2 pos = IntVector2.of(0, 0);

        builder.setActive(pos);
        assertThat(builder.isActive(pos)).isTrue();
    }

    @Test
    void BuilderRemove() {
        ConwayBoard.Builder builder = ConwayBoard.builder();
        IntVector2 pos = IntVector2.of(0, 0);

        builder.setActive(pos);
        builder.setInactive(pos);
        assertThat(builder.isActive(pos)).isFalse();
    }

    @Test
    void BuilderRemoveUnsetPos() {
        ConwayBoard.Builder builder = ConwayBoard.builder();
        IntVector2 pos = IntVector2.of(0, 0);

        builder.setInactive(pos);
        assertThat(builder.isActive(pos)).isFalse();
    }

    @Test
    void BuilderActiveUnsetPos() {
        ConwayBoard.Builder builder = ConwayBoard.builder();
        IntVector2 pos = IntVector2.of(0, 0);

        assertThat(builder.isActive(pos)).isFalse();
    }

    @Test
    void BuilderSetStateActive() {
        ConwayBoard.Builder builder = ConwayBoard.builder();
        IntVector2 pos = IntVector2.of(0, 0);

        builder.setState(pos, true);

        assertThat(builder.isActive(pos)).isTrue();
    }

    @Test
    void BuilderSetStateInactive() {
        ConwayBoard.Builder builder = ConwayBoard.builder();
        IntVector2 pos = IntVector2.of(0, 0);

        builder.setActive(pos);
        builder.setState(pos, false);

        assertThat(builder.isActive(pos)).isFalse();
    }

    @Test
    void BuilderGetPoints() {
        ConwayBoard.Builder builder = ConwayBoard.builder();
        ImmutableList<IntVector2> points = ImmutableList.of(
                IntVector2.of(0, 0),
                IntVector2.of(42, 42)
        );

        for (IntVector2 pos : points) {
            builder.setActive(pos);
        }

        assertThat(builder.getPoints()).containsExactlyElementsIn(points);
    }

    @Test
    void BuilderGetPointsWithMutations() {
        ConwayBoard.Builder builder = ConwayBoard.builder();
        IntVector2 one = IntVector2.of(0, 1);
        IntVector2 two = IntVector2.of(0, 2);
        IntVector2 three = IntVector2.of(0, 3);

        builder.setActive(one);
        assertThat(builder.getPoints()).containsExactly(one);

        builder.setActive(two);
        assertThat(builder.getPoints()).containsExactly(one, two);

        builder.setActive(three);
        assertThat(builder.getPoints()).containsExactly(one, two, three);

        builder.setInactive(one);
        assertThat(builder.getPoints()).containsExactly(two, three);
    }

    @Test
    void BuilderBuild() {
        ConwayBoard.Builder builder = ConwayBoard.builder();
        IntVector2 one = IntVector2.of(0, 0);
        IntVector2 two = IntVector2.of(42, 42);

        builder.setActive(one);
        builder.setActive(two);

        ConwayBoard board = builder.build();

        assertThat(board.getPoints()).containsExactlyElementsIn(builder.getPoints());
        assertThat(board.getPoints()).containsExactly(one, two);

        assertThat(board.isActive(one)).isTrue();
        assertThat(board.isActive(two)).isTrue();
    }

    @Test
    void evolve2neighborsToEmpty() {
        ConwayBoard board = ConwayBoard.builder()
                .setActive(IntVector2.of(0, 1))
                .setActive(IntVector2.of(0, -1))
                .build();

        ConwayBoard nextGen = board.evolve();

        assertThat(nextGen.getPoints()).isEmpty();
    }

    @Test
    void evolveSinglePoints() {
        ConwayBoard board = ConwayBoard.builder()
                .setActive(IntVector2.of(0, 0))
                .build();

        ConwayBoard nextGen = board.evolve();

        assertThat(nextGen.getPoints()).isEmpty();
    }

    @Test
    void evolveVerticalLineBlinker() {
        ConwayBoard board = ConwayBoard.builder()
                .setActive(IntVector2.of(0, -1))
                .setActive(IntVector2.of(0, 0))
                .setActive(IntVector2.of(0, 1))
                .build();

        ConwayBoard nextGen = board.evolve();

        assertThat(nextGen.getPoints()).containsExactly(
                IntVector2.of(-1, 0),
                IntVector2.of(0, 0),
                IntVector2.of(1, 0)
        );
    }

    @Test
    void evolveHorzLineBlinker() {
        ConwayBoard board = ConwayBoard.builder()
                .setActive(IntVector2.of(-1, 0))
                .setActive(IntVector2.of(0, 0))
                .setActive(IntVector2.of(1, 0))
                .build();

        ConwayBoard nextGen = board.evolve();

        assertThat(nextGen.getPoints()).containsExactly(
                IntVector2.of(0, -1),
                IntVector2.of(0, 0),
                IntVector2.of(0, 1)
        );
    }

    @Test
    void builderToString() {
        ConwayBoard.Builder board = ConwayBoard.builder()
                .setActive(IntVector2.of(0, 1))
                .setActive(IntVector2.of(1, 0))
                .setActive(IntVector2.of(1, -1))
                .setActive(IntVector2.of(0, -1))
                .setActive(IntVector2.of(-1, -1));

        assertThat(board.toString()).isEqualTo(""
                + " X \n"
                + "  X\n"
                + "XXX\n"
                + "");
    }

    @Test
    void boardToString() {
        ConwayBoard board = ConwayBoard.builder()
                .setActive(IntVector2.of(0, 1))
                .setActive(IntVector2.of(1, 0))
                .setActive(IntVector2.of(1, -1))
                .setActive(IntVector2.of(0, -1))
                .setActive(IntVector2.of(-1, -1))
                .build();

        assertThat(board.toString()).isEqualTo(""
                + " X \n"
                + "  X\n"
                + "XXX\n"
                + "");
    }

    @Test
    void evolveSimpleGlider() {
        ConwayBoard board = ConwayBoard.builder()
                .setActive(IntVector2.of(0, 1))
                .setActive(IntVector2.of(1, 0))
                .setActive(IntVector2.of(1, -1))
                .setActive(IntVector2.of(0, -1))
                .setActive(IntVector2.of(-1, -1))
                .build();

        ConwayBoard firstStep = board.evolve();
        assertThat(firstStep.getPoints()).containsExactly(
                IntVector2.of(0, -1),
                IntVector2.of(0, -2),
                IntVector2.of(1, -1),
                IntVector2.of(-1, 0),
                IntVector2.of(1, 0)
        );

        ConwayBoard secondStep = firstStep.evolve();
        assertThat(secondStep.getPoints()).containsExactly(
                IntVector2.of(0, -2),
                IntVector2.of(1, -2),
                IntVector2.of(1, -1),
                IntVector2.of(1, 0),
                IntVector2.of(-1, -1)
        );

        ConwayBoard thirdStep = secondStep.evolve();
        assertThat(thirdStep.getPoints()).containsExactly(
                IntVector2.of(0, 0),
                IntVector2.of(0, -2),
                IntVector2.of(1, -2),
                IntVector2.of(2, -1),
                IntVector2.of(1, -1)
        );

        ConwayBoard fourthStep = thirdStep.evolve();
        assertThat(fourthStep.getPoints()).containsExactly(
                IntVector2.of(0, -2),
                IntVector2.of(2, -2),
                IntVector2.of(1, -2),
                IntVector2.of(2, -1),
                IntVector2.of(1, 0)
        );
    }
}