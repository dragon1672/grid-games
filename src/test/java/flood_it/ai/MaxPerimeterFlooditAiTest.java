package flood_it.ai;

import common.board.BoardLoaders;
import flood_it.game.FloodColor;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static flood_it.ai.TestUtils.getBoardFromString;

class MaxPerimeterFlooditAiTest {

    @Test
    void getSquare() {
        assertThat(new MaxPerimeterFlooditAi().getMove(getBoardFromString("" +
                "RB\n" +
                "BB\n" +
                ""))).isEqualTo(FloodColor.BLUE);
    }

    @Test
    void getSquare_theLastMove() {
        assertThat(new MaxPerimeterFlooditAi().getMove(getBoardFromString("" +
                "RRR\n" +
                "RBR\n" +
                "RRR\n" +
                ""))).isEqualTo(FloodColor.BLUE);
    }

    @Test
    void getSquare_theIssue() {
        // Blue would be the better choice to complete the game, but it would decrease the perimeter.
        // We pick available moves based off what colors are on the board, so green is a valid choice,
        // even though it will have 0 progression

        // This isn't an bug with the AI, but now it works, the premise of maximizing perimeter leads to this issue.
        assertThat(new MaxPerimeterFlooditAi().getMove(getBoardFromString("" +
                "RRRRRBR\n" +
                "RBRRRBG\n" +
                "RRRRRBR\n" +
                "RRRRRBR\n" +
                "RRRRRBR\n" +
                ""))).isEqualTo(FloodColor.GREEN);
    }

    @Test
    void getBoardPerimeter_single() {
        assertThat(MaxPerimeterFlooditAi.getBoardPerimeter(BoardLoaders.generateFromString("" +
                "R--\n" +
                "---\n" +
                ""))).isEqualTo(4);
    }

    @Test
    void getBoardPerimeter_square() {
        assertThat(MaxPerimeterFlooditAi.getBoardPerimeter(BoardLoaders.generateFromString("" +
                "RR--\n" +
                "RR--\n" +
                "----\n" +
                ""))).isEqualTo(8);
    }

    @Test
    void getBoardPerimeter_line() {
        assertThat(MaxPerimeterFlooditAi.getBoardPerimeter(BoardLoaders.generateFromString("" +
                "RR--\n" +
                "-R--\n" +
                "-R--\n" +
                "----\n" +
                ""))).isEqualTo(10);
    }
}