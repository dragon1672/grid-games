package common.utils;

import com.google.common.collect.ImmutableSet;
import common.board.Board;
import common.board.BoardImpl;
import common.board.BoardLoaders;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.truth.Truth.assertThat;

class BoardUtilsTest {

    private void testConnectedCells(String boardStr, IntVector2 startingPos, ImmutableSet<IntVector2> expectedPositions) {
        Board<Character> board = BoardLoaders.generateFromString(boardStr);
        Set<IntVector2> resultingConnectedCells = BoardUtils.getConnectedCells(board, startingPos);
        assertThat(resultingConnectedCells).containsExactlyElementsIn(expectedPositions);
    }

    @Test
    void getConnectedCells_singleCell() throws Exception {
        testConnectedCells("" +
                        "M",
                IntVector2.of(0, 0),
                ImmutableSet.of(
                        IntVector2.of(0, 0)
                ));
    }

    @Test
    void getConnectedCells_border() throws Exception {
        testConnectedCells("" +
                        "MMM\n" +
                        "M-M\n" +
                        "MMM",
                IntVector2.of(0, 0),
                ImmutableSet.of(
                        IntVector2.of(0, 0), IntVector2.of(1, 0), IntVector2.of(2, 0),
                        IntVector2.of(0, 1), IntVector2.of(2, 1),
                        IntVector2.of(0, 2), IntVector2.of(1, 2), IntVector2.of(2, 2)
                ));
    }

    @Test
    void getConnectedCells_singleSurrounded() throws Exception {
        testConnectedCells("" +
                        "---\n" +
                        "-M-\n" +
                        "---",
                IntVector2.of(1, 1),
                ImmutableSet.of(
                        IntVector2.of(1, 1)
                ));
    }

    @Test
    void getConnectedCells_blockSurrounded() throws Exception {
        testConnectedCells("" +
                        "----\n" +
                        "-MM-\n" +
                        "-MM-\n" +
                        "----",
                IntVector2.of(1, 1),
                ImmutableSet.of(
                        IntVector2.of(1, 1), IntVector2.of(2, 1),
                        IntVector2.of(1, 2), IntVector2.of(2, 2)
                ));
    }

    @Test
    void getConnectedCells_disconnectedDiagonal() throws Exception {
        testConnectedCells("" +
                        "M-\n" +
                        "-M",
                IntVector2.of(0, 0),
                ImmutableSet.of(
                        IntVector2.of(0, 0)
                ));
    }

    @Test
    void getConnectedCells_diagonal() throws Exception {
        testConnectedCells("" +
                        "MM-----\n" +
                        "-MM----\n" +
                        "--MM---\n" +
                        "---MM--",
                IntVector2.of(0, 0),
                ImmutableSet.of(
                        IntVector2.of(0, 0), IntVector2.of(1, 0),
                        IntVector2.of(1, 1), IntVector2.of(2, 1),
                        IntVector2.of(2, 2), IntVector2.of(3, 2),
                        IntVector2.of(3, 3), IntVector2.of(4, 3)
                ));
    }

    @Test
    void getConnectedCells_hShape() throws Exception {
        testConnectedCells("" +
                        "M--M\n" +
                        "MMMM\n" +
                        "M--M",
                IntVector2.of(0, 0),
                ImmutableSet.of(
                        IntVector2.of(0, 0), IntVector2.of(3, 0),
                        IntVector2.of(0, 1), IntVector2.of(1, 1), IntVector2.of(2, 1), IntVector2.of(3, 1),
                        IntVector2.of(0, 2), IntVector2.of(3, 2)
                ));
    }

    @Test
    void getConnectedCells_singleLine() throws Exception {
        testConnectedCells("" +
                        "----\n" +
                        "MMMM\n" +
                        "----",
                IntVector2.of(0, 1),
                ImmutableSet.of(
                        IntVector2.of(0, 1), IntVector2.of(1, 1), IntVector2.of(2, 1), IntVector2.of(3, 1)
                ));
    }

    @Test
    void getConnectedCells_doubleLine() throws Exception {
        testConnectedCells("" +
                        "----\n" +
                        "MMMM\n" +
                        "MMMM\n" +
                        "----",
                IntVector2.of(0, 1),
                ImmutableSet.of(
                        IntVector2.of(0, 1), IntVector2.of(1, 1), IntVector2.of(2, 1), IntVector2.of(3, 1),
                        IntVector2.of(0, 2), IntVector2.of(1, 2), IntVector2.of(2, 2), IntVector2.of(3, 2)
                ));
    }

    @Test
    void getConnectedCells_squiggle() throws Exception {
        testConnectedCells("" +
                        "M-MMM\n" +
                        "M-M-M\n" +
                        "MMM-M",
                IntVector2.of(0, 0),
                ImmutableSet.of(
                        IntVector2.of(0, 0),
                        IntVector2.of(0, 1),
                        IntVector2.of(0, 2), IntVector2.of(1, 2), IntVector2.of(2, 2),
                        IntVector2.of(2, 1),
                        IntVector2.of(2, 0), IntVector2.of(3, 0), IntVector2.of(4, 0),
                        IntVector2.of(4, 1),
                        IntVector2.of(4, 2)
                ));
    }

    @Test
    void boardPositionsAsStream_oneByOne() throws Exception {
        Board<Character> board = BoardImpl.make(1, 1);
        assertThat(BoardUtils.boardPositionsAsStream(board).collect(toImmutableList()))
                .containsExactlyElementsIn(ImmutableSet.of(
                        IntVector2.of(0, 0)
                ));
    }

    @Test
    void boardPositionsAsStream_smallBoard() throws Exception {
        Board<Character> board = BoardImpl.make(2, 2);

        assertThat(BoardUtils.boardPositionsAsStream(board).collect(toImmutableList()))
                .containsExactlyElementsIn(ImmutableSet.of(
                        IntVector2.of(0, 0), IntVector2.of(1, 0),
                        IntVector2.of(0, 1), IntVector2.of(1, 1)
                ));
    }

    @Test
    void boardPositionsAsStream() throws Exception {
        Board<Character> board = BoardImpl.make(4, 4);

        assertThat(BoardUtils.boardPositionsAsStream(board).collect(toImmutableList()))
                .containsExactlyElementsIn(ImmutableSet.of(
                        IntVector2.of(0, 0), IntVector2.of(1, 0), IntVector2.of(2, 0), IntVector2.of(3, 0),
                        IntVector2.of(0, 1), IntVector2.of(1, 1), IntVector2.of(2, 1), IntVector2.of(3, 1),
                        IntVector2.of(0, 2), IntVector2.of(1, 2), IntVector2.of(2, 2), IntVector2.of(3, 2),
                        IntVector2.of(0, 3), IntVector2.of(1, 3), IntVector2.of(2, 3), IntVector2.of(3, 3)
                ));
    }

    @Test
    void boardPositions_oneByOne() throws Exception {
        Board<Character> board = BoardImpl.make(1, 1);

        assertThat(BoardUtils.boardPositions(board)).containsExactlyElementsIn(ImmutableSet.of(
                IntVector2.of(0, 0)
        ));
    }

    @Test
    void boardPositions_smallBoard() throws Exception {
        Board<Character> board = BoardImpl.make(2, 2);

        assertThat(BoardUtils.boardPositions(board)).containsExactlyElementsIn(ImmutableSet.of(
                IntVector2.of(0, 0), IntVector2.of(1, 0),
                IntVector2.of(0, 1), IntVector2.of(1, 1)
        ));
    }

    @Test
    void boardPositions() throws Exception {
        Board<Character> board = BoardImpl.make(4, 4);

        assertThat(BoardUtils.boardPositions(board)).containsExactlyElementsIn(ImmutableSet.of(
                IntVector2.of(0, 0), IntVector2.of(1, 0), IntVector2.of(2, 0), IntVector2.of(3, 0),
                IntVector2.of(0, 1), IntVector2.of(1, 1), IntVector2.of(2, 1), IntVector2.of(3, 1),
                IntVector2.of(0, 2), IntVector2.of(1, 2), IntVector2.of(2, 2), IntVector2.of(3, 2),
                IntVector2.of(0, 3), IntVector2.of(1, 3), IntVector2.of(2, 3), IntVector2.of(3, 3)
        ));
    }

    @Test
    void isUniformColor_oneByOneBoard() throws Exception {
        assertThat(BoardUtils.isUniformColor(BoardLoaders.generateFromString("A"))).isTrue();
    }

    @Test
    void isUniformColor() throws Exception {
        Board<Character> board = BoardLoaders.generateFromString("" +
                "AAA\n" +
                "AAA\n" +
                "AAA\n" +
                "");

        assertThat(BoardUtils.isUniformColor(board)).isTrue();
    }

    @Test
    void cellTypesOnBoard_oneByOne() throws Exception {
        Set<Character> uniqueCells = BoardUtils.cellTypesOnBoard(BoardLoaders.generateFromString("A"));
        assertThat(uniqueCells).containsExactly('A');
    }

    @Test
    void cellTypesOnBoard_uniform() throws Exception {
        Set<Character> uniqueCells = BoardUtils.cellTypesOnBoard(BoardLoaders.generateFromString("" +
                "AAA\n" +
                "AAA\n" +
                "AAA" +
                ""));
        assertThat(uniqueCells).containsExactly('A');
    }

    @Test
    void cellTypesOnBoard_allUnique() throws Exception {
        Set<Character> uniqueCells = BoardUtils.cellTypesOnBoard(BoardLoaders.generateFromString("" +
                "ABC\n" +
                "DEF\n" +
                "GHI" +
                ""));
        assertThat(uniqueCells).containsExactly('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I');
    }

    @Test
    void cellTypesOnBoard_mixed() throws Exception {
        Set<Character> uniqueCells = BoardUtils.cellTypesOnBoard(BoardLoaders.generateFromString("" +
                "ABC\n" +
                "CAB\n" +
                "BCB" +
                ""));
        assertThat(uniqueCells).containsExactly('A', 'B', 'C');
    }
}