package popit;

import com.google.common.collect.ImmutableBiMap;
import common.board.Board;
import common.board.BoardImpl;
import popit.game.BlockColor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class BoardLoaders {
    private static final ImmutableBiMap<Character, BlockColor> block2Char = ImmutableBiMap.<Character, BlockColor>builder()
            .put('R', BlockColor.RED)
            .put('G', BlockColor.GREEN)
            .put('Y', BlockColor.YELLOW)
            .put('B', BlockColor.BROWN)
            .put('P', BlockColor.PURPLE)
            .put('I', BlockColor.INDIGO)
            .put(' ', BlockColor.WHITES_INVALID)
            .build();

    private static final ImmutableBiMap<Integer, BlockColor> block2RGB = ImmutableBiMap.<Integer, BlockColor>builder()
            .put(-696218, BlockColor.RED)
            .put(-13260140, BlockColor.GREEN)
            .put(-7569813, BlockColor.BROWN)
            .put(-5741590, BlockColor.PURPLE)
            .put(-11236113, BlockColor.INDIGO)
            .put(-677581, BlockColor.YELLOW)
            .build();

    public static Board<BlockColor> generateFromString(String boardStr) {
        String[] lines = boardStr.split("\\n");
        checkNotNull(boardStr);
        checkArgument(lines.length > 0, "expected non empty string");
        checkArgument(Stream.of(lines).mapToInt(String::length).distinct().count() == 1, "All lines expected to have the same length");

        int width = lines[0].length();
        int height = lines.length;

        Board<BlockColor> board = BoardImpl.make(width, height);

        for (int y = 0; y < lines.length; y++) {
            for (int x = 0; x < lines[y].length(); x++) {
                BlockColor blockColor = block2Char.getOrDefault(lines[y].charAt(x), BlockColor.WHITES_INVALID);
                board.set(blockColor, x, y);
            }
        }

        return board;
    }

    private static final Random rand = new Random();

    public static Board<BlockColor> generateRandomBoard(int width, int height) {
        return generateRandomBoard(width, height, BlockColor.RED, BlockColor.GREEN, BlockColor.YELLOW, BlockColor.INDIGO);
    }

    public static Board<BlockColor> generateRandomBoard(int width, int height, BlockColor... possibleColors) {
        Board<BlockColor> generatedBoard = BoardImpl.make(width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                BlockColor randomBlockColor = possibleColors[rand.nextInt(possibleColors.length)];
                generatedBoard.set(randomBlockColor, x, y);
            }
        }
        return generatedBoard;
    }

    /**
     * Generate a board from a given image screenshot from facebook
     *
     * @param path   path to image file
     * @param width  width
     * @param height height
     * @return builder pattern instance of same BoardGenerator called
     * @throws IOException if there are issues loading the image
     */
    public static Board<BlockColor> generateFromImage(String path, int width, int height) throws IOException {
        BufferedImage img = ImageIO.read(new File(path));
        StringBuilder boardStr = new StringBuilder((width + 1) * height);
        int cellWidth = img.getWidth() / width;
        int cellHeight = img.getHeight() / height;
        int cellXOffset = cellWidth / 2;
        int cellYOffset = cellHeight / 2;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int correctedX = x * cellWidth + cellXOffset;
                int correctedY = y * cellHeight + cellYOffset;
                int rgb = img.getRGB(correctedX, correctedY);
                BlockColor color = block2RGB.getOrDefault(rgb, BlockColor.WHITES_INVALID);
                boardStr.append(block2Char.inverse().getOrDefault(color, ' '));
            }
            if (y != height - 1) {
                boardStr.append('\n');
            }
        }
        return generateFromString(boardStr.toString());
    }
}
