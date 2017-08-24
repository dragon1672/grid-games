package flood_it.ai;

import flood_it.game.FloodColor;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static flood_it.ai.TestUtils.getBoardFromString;

class MaxAreaFlooditAiTest {

    @Test
    void getSquare() {
        assertThat(new MaxAreaFlooditAi().getMove(getBoardFromString("" +
                "RB\n" +
                "BB\n" +
                ""))).isEqualTo(FloodColor.BLUE);
    }

    @Test
    void getSquare_theLastMove() {
        assertThat(new MaxAreaFlooditAi().getMove(getBoardFromString("" +
                "RRR\n" +
                "RBR\n" +
                "RRR\n" +
                ""))).isEqualTo(FloodColor.BLUE);
    }

    @Test
    void getSquare_whatBreaksPerimeter() {
        assertThat(new MaxAreaFlooditAi().getMove(getBoardFromString("" +
                "RRRRRBR\n" +
                "RBRRRBG\n" +
                "RRRRRBR\n" +
                "RRRRRBR\n" +
                "RRRRRBR\n" +
                ""))).isEqualTo(FloodColor.BLUE);
    }

    @Test
    void getSquare_maxArea() {
        assertThat(new MaxAreaFlooditAi().getMove(getBoardFromString("" +
                "RGB\n" +
                "BBB\n" +
                ""))).isEqualTo(FloodColor.BLUE);
    }

}