package flood_it.ai;

import com.google.common.collect.ImmutableBiMap;
import common.board.BoardLoaders;
import common.board.ReadOnlyBoard;
import flood_it.game.FloodColor;

public class TestUtils {

    private static ImmutableBiMap<Character, FloodColor> boardCharConverter = ImmutableBiMap.<Character, FloodColor>builder()
            .put(FloodColor.RED.toString().charAt(0), FloodColor.RED)
            .put(FloodColor.YELLOW.toString().charAt(0), FloodColor.YELLOW)
            .put(FloodColor.GREEN.toString().charAt(0), FloodColor.GREEN)
            .put(FloodColor.LIGHT_BLUE.toString().charAt(0), FloodColor.LIGHT_BLUE)
            .put(FloodColor.BLUE.toString().charAt(0), FloodColor.BLUE)
            .put(FloodColor.PURPLE.toString().charAt(0), FloodColor.PURPLE)
            .build();

    static ReadOnlyBoard<FloodColor> getBoardFromString(String boardStr) {
        return BoardLoaders.generateFromString(boardStr, boardCharConverter::get);
    }
}
