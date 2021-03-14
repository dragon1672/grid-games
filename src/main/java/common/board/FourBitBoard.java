package common.board;


import com.google.common.collect.ImmutableBiMap;

import java.util.Arrays;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Optimized for fast copying, bit board will store data in a long[]
 *
 * @param <T>
 */
public class FourBitBoard<T> implements Board<T> {
    private final ImmutableBiMap<Byte, T> pieceToByteConverter;

    private final static int BIT_SIZE_OF_PIECE = 4;
    private final static int BITS_IN_LONG = 4 * 8;
    private final static int PIECES_PER_LONG = BITS_IN_LONG / BIT_SIZE_OF_PIECE;
    private final static long PIECE_VALUE_MASK = 0xF; // pieces take up 4 bits

    private final int width;
    private final int height;
    private final T defaultPiece;
    private final long[] data;


    public FourBitBoard(ImmutableBiMap<Byte, T> pieceToByteConverter, T defaultPiece, int width, int height) {
        this.pieceToByteConverter = checkNotNull(pieceToByteConverter);
        this.defaultPiece = checkNotNull(defaultPiece);
        checkArgument(width > 0, "width must be greater than 0");
        this.width = width;
        checkArgument(height > 0, "height must be greater than 0");
        this.height = height;
        if (pieceToByteConverter.keySet().stream().anyMatch(key -> key >= PIECE_VALUE_MASK)) {
            throw new IllegalArgumentException(String.format("Pieces can only be mapped to %d bits: %s", BIT_SIZE_OF_PIECE, PIECE_VALUE_MASK));
        }
        float totalPieces = width * height;
        data = new long[(int) Math.ceil(totalPieces / PIECES_PER_LONG)];
    }

    // private used for cloning, no validation checks.
    private FourBitBoard(ImmutableBiMap<Byte, T> pieceToByteConverter, T defaultPiece, int width, int height, long[] data) {
        this.pieceToByteConverter = checkNotNull(pieceToByteConverter);
        this.defaultPiece = checkNotNull(defaultPiece);
        checkArgument(width > 0, "width must be greater than 0");
        this.width = width;
        checkArgument(height > 0, "height must be greater than 0");
        this.height = height;
        float totalPieces = width * height;
        int totalDataPointsNeeded = (int) Math.ceil(totalPieces / PIECES_PER_LONG);
        checkArgument(data.length >= totalDataPointsNeeded, "Data must have at least %d entries to hold width and height requirements", totalDataPointsNeeded);
        this.data = checkNotNull(data);
    }

    private int getSequentialIndex(int x, int y) {
        return y * width + x;
    }

    private int getBoardArrayIndex(int x, int y) {
        return getSequentialIndex(x, y) / PIECES_PER_LONG;
    }

    private int getBitOffset(int x, int y) {
        return (getSequentialIndex(x, y) - PIECES_PER_LONG * getBoardArrayIndex(x, y)) * BIT_SIZE_OF_PIECE;
    }

    @Override
    public void set(T block, int x, int y) {
        if (!isValidPos(x, y)) {
            throw new IllegalArgumentException(String.format("Invalid pos {%d, %d}", x, y));
        }
        int boardBitOffset = getBitOffset(x, y);

        long pieceValue = pieceToByteConverter.inverse().get(block);
        long shiftedPieceValue = pieceValue << boardBitOffset;
        long mask = ~(PIECE_VALUE_MASK << boardBitOffset); // to 0 out the appropriate section

        int boardDataIndex = getBoardArrayIndex(x, y);
        data[boardDataIndex] &= mask;
        data[boardDataIndex] |= shiftedPieceValue;
    }

    @Override
    public T get(int x, int y) {
        if (!isValidPos(x, y)) {
            throw new IllegalArgumentException(String.format("Invalid pos {%d, %d}", x, y));
        }
        int boardDataIndex = getBoardArrayIndex(x, y);
        int adjustedIndex = (getSequentialIndex(x, y) % PIECES_PER_LONG) * BIT_SIZE_OF_PIECE;
        long mask = PIECE_VALUE_MASK << adjustedIndex;
        byte pieceData = (byte) ((data[boardDataIndex] & mask) >> adjustedIndex & PIECE_VALUE_MASK);
        return pieceToByteConverter.getOrDefault(pieceData, defaultPiece);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public FourBitBoard<T> duplicate() {
        return new FourBitBoard<>(pieceToByteConverter, defaultPiece, width, height, data.clone());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FourBitBoard<?> that = (FourBitBoard<?>) o;
        return width == that.width && height == that.height && pieceToByteConverter.equals(that.pieceToByteConverter) && defaultPiece.equals(that.defaultPiece) && Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(pieceToByteConverter, width, height, defaultPiece);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public String toString() {
        return asString();
    }
}
