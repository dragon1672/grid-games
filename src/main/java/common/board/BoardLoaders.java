package common.board;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Extra Utilities for creating boards from various input
 */
public class BoardLoaders {
    public static Board<Character> generateFromString(String boardStr) {
        return generateFromString(boardStr, Function.identity());
    }

    public static <T> Board<T> generateFromString(String boardStr, Function<Character, T> char2Block) {
        String[] lines = boardStr.split("\\n");
        checkNotNull(boardStr);
        checkArgument(lines.length > 0, "expected non empty string");
        checkArgument(Stream.of(lines).mapToInt(String::length).distinct().count() == 1, "All lines expected to have the same length");

        int width = lines[0].length();
        int height = lines.length;

        Board<T> board = BoardImpl.make(width, height);

        for (int y = 0; y < lines.length; y++) {
            for (int x = 0; x < lines[y].length(); x++) {
                T blockColor = char2Block.apply(lines[y].charAt(x));
                board.set(blockColor, x, y);
            }
        }

        return board;
    }

    /**
     * Generate a board from a given image
     *
     * @param path   path to image file
     * @param width  width
     * @param height height
     * @return builder pattern instance of same BoardGenerator called
     * @throws IOException if there are issues loading the image
     */
    public static <T> Board<T> generateFromImage(String path, int width, int height, Function<Integer, T> rgbToCell) throws IOException {
        BufferedImage img = ImageIO.read(new File(path));
        int cellWidth = img.getWidth() / width;
        int cellHeight = img.getHeight() / height;
        int cellXOffset = cellWidth / 2;
        int cellYOffset = cellHeight / 2;

        Board<T> board = BoardImpl.make(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int correctedX = x * cellWidth + cellXOffset;
                int correctedY = y * cellHeight + cellYOffset;
                int rgb = img.getRGB(correctedX, correctedY);
                T block = rgbToCell.apply(rgb);
                board.set(block, x, y);
            }
        }
        return board;
    }
}
