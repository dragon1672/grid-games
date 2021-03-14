package common.board;

import com.google.common.collect.ImmutableBiMap;
import common.utils.IntVector2;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FourBitBoardTest {

    private enum GenericTestEnum {
        DEFAULT, ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, ELEVEN, TWELVE;

        private final static ImmutableBiMap<Byte, GenericTestEnum> defaultByteConverter = ImmutableBiMap.<Byte, GenericTestEnum>builder()
                // Starts zeroed out, so matching 0 will 'override' default
                //.put((byte) 0x0, GenericTestEnum.ZERO)
                .put((byte) 0x1, GenericTestEnum.ONE)
                .put((byte) 0x2, GenericTestEnum.TWO)
                .put((byte) 0x3, GenericTestEnum.THREE)
                .put((byte) 0x4, GenericTestEnum.FOUR)
                .put((byte) 0x5, GenericTestEnum.FIVE)
                .put((byte) 0x6, GenericTestEnum.SIX)
                .put((byte) 0x7, GenericTestEnum.SEVEN)
                .put((byte) 0x8, GenericTestEnum.EIGHT)
                .put((byte) 0x9, GenericTestEnum.NINE)
                .put((byte) 0xA, GenericTestEnum.TEN)
                .put((byte) 0xB, GenericTestEnum.ELEVEN)
                .put((byte) 0xC, GenericTestEnum.TWELVE)
                .build();
    }

    @Test
    public void newBoardIsEmpty() {
        FourBitBoard<GenericTestEnum> board = new FourBitBoard<>(GenericTestEnum.defaultByteConverter, GenericTestEnum.DEFAULT, 3, 3);
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                assertThat(board.get(x, y)).named("location %s", IntVector2.of(x, y)).isEqualTo(GenericTestEnum.DEFAULT);
            }
        }
    }

    @Test
    public void newBoardIsWithZeroMapping() {
        // values are initially zeroed out, so a fresh board will match to zero
        ImmutableBiMap<Byte, GenericTestEnum> byteConverter = ImmutableBiMap.of((byte) 0x0, GenericTestEnum.ZERO);
        FourBitBoard<GenericTestEnum> board = new FourBitBoard<>(byteConverter, GenericTestEnum.DEFAULT, 3, 3);
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                assertThat(board.get(x, y)).named("location %s", IntVector2.of(x, y)).isEqualTo(GenericTestEnum.ZERO);
            }
        }
    }

    @Test
    public void getOutOfBounds() {
        FourBitBoard<GenericTestEnum> board = new FourBitBoard<>(GenericTestEnum.defaultByteConverter, GenericTestEnum.DEFAULT, 3, 3);
        assertThrows(IllegalArgumentException.class, () -> board.get(1, 5));
        assertThrows(IllegalArgumentException.class, () -> board.get(5, 1));
        assertThrows(IllegalArgumentException.class, () -> board.get(5, 5));
    }

    @Test
    public void getOutOfBounds_negative() {
        FourBitBoard<GenericTestEnum> board = new FourBitBoard<>(GenericTestEnum.defaultByteConverter, GenericTestEnum.DEFAULT, 3, 3);
        assertThrows(IllegalArgumentException.class, () -> board.get(-1, 1));
        assertThrows(IllegalArgumentException.class, () -> board.get(1, -1));
        assertThrows(IllegalArgumentException.class, () -> board.get(-1, -1));
    }

    @Test
    void duplicate() {
        FourBitBoard<GenericTestEnum> board = new FourBitBoard<>(GenericTestEnum.defaultByteConverter, GenericTestEnum.DEFAULT, 3, 3);
        board.set(GenericTestEnum.ONE, 0, 0);
        FourBitBoard<GenericTestEnum> dup = board.duplicate();
        // should be equal as nothing has changed
        assertThat(board).isEqualTo(dup);

        // Assert copy doesn't change if original is changed
        board.set(GenericTestEnum.TWO, 1, 1);
        assertThat(board).isNotEqualTo(dup);
        assertThat(board.get(0, 0)).isEqualTo(GenericTestEnum.ONE);
        assertThat(board.get(1, 1)).isEqualTo(GenericTestEnum.TWO);
        assertThat(dup.get(0, 0)).isEqualTo(GenericTestEnum.ONE);
        assertThat(dup.get(1, 1)).isEqualTo(GenericTestEnum.DEFAULT);

        // Assert copy doesn't mutate original
        dup.set(GenericTestEnum.THREE, 0, 0);
        assertThat(board).isNotEqualTo(dup);
        assertThat(board.get(0, 0)).isEqualTo(GenericTestEnum.ONE);
        assertThat(dup.get(0, 0)).isEqualTo(GenericTestEnum.THREE);
    }

    @Test
    public void duplicateStressTest() {
        FourBitBoard<GenericTestEnum> board = new FourBitBoard<>(GenericTestEnum.defaultByteConverter, GenericTestEnum.DEFAULT, 30, 30);
        Random rand = new Random(42); // make the test stable
        GenericTestEnum[] values = GenericTestEnum.defaultByteConverter.values().toArray(new GenericTestEnum[0]);
        for (int i = 0; i < 100; i++) {
            GenericTestEnum randomPiece = values[rand.nextInt(values.length)];
            int x = rand.nextInt(board.getWidth());
            int y = rand.nextInt(board.getHeight());
            board.set(randomPiece, x, y);
            FourBitBoard<GenericTestEnum> dup = board.duplicate();
            assertThat(board).isEqualTo(dup);
        }
    }
}