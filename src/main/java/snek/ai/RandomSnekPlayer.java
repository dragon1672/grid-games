package snek.ai;

import com.google.common.collect.ImmutableList;
import common.board.ReadOnlyBoard;
import common.utils.Flogger;
import common.utils.IntVector2;
import common.utils.RandomUtils;
import snek.game.Direction;
import snek.game.SnekBody;
import snek.game.SnekCell;
import snek.game.SnekGame;

import java.util.Optional;

public class RandomSnekPlayer implements SnekPlayer {
    private static final Flogger logger = Flogger.getInstance();

    private static final int DELAY = 100;
    private static final ImmutableList<Direction> DIRECTIONS = ImmutableList.of(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT);

    private Optional<Direction> randomValidMove(SnekBody snek, ReadOnlyBoard<SnekCell> board) {
        return RandomUtils.randomizeStream(DIRECTIONS.stream()
                .filter(direction -> {
                    IntVector2 pos = snek.getHead().add(direction.vector);
                    return board.isValidPos(pos)
                            && !snek.getBodyPositions().contains(pos);
                }))
                .findFirst();
    }

    private Direction trueRandomDir() {
        return RandomUtils.randomFromList(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT);
    }

    @Override
    public void play(SnekGame game) {
        logger.atInfo().log("ya know I tried");
        while (!game.isComplete()) {
            Optional<Direction> randomValidMove = randomValidMove(game.getSnek(), game.getBoard());

            game.move(randomValidMove.orElse(trueRandomDir()));


            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                logger.atError().log(e.getMessage());
            }
        }
    }
}
