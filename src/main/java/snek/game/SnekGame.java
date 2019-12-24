package snek.game;

import common.board.ReadOnlyBoard;
import common.interfaces.Game;
import common.utils.BoardUtils;
import common.utils.Flogger;
import common.utils.IntVector2;
import common.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * MineSweeper game
 */
public class SnekGame implements Game<SnekCell> {
    private static final Flogger logger = Flogger.getInstance();

    private enum GameState {
        Running, SnekDead, GameWon;
    }

    private final List<Consumer<ReadOnlyBoard<SnekCell>>> boardSubscribers = new ArrayList<>();
    private GameState gameState = GameState.Running;
    private final SnekBoard board;
    private final MoveableSnekBody snekBody;
    private final int appleIncrease;

    public SnekGame(int width, int height, IntVector2 startPos, IntVector2 startingApple, int bodyLength, int appleIncrease) {
        this.appleIncrease = appleIncrease;
        snekBody = new MoveableSnekBody(startPos, bodyLength);
        board = new SnekBoard(width, height, startingApple, snekBody);
    }

    public SnekBody getSnek() {
        return snekBody;
    }

    public IntVector2 getApplePos() {
        return board.getApplePos();
    }

    public void move(Direction dir) {
        if (gameState != GameState.Running) {
            logger.atInfo().log("Attempting move %s when game is already complete in state %s", dir, gameState);
            return;
        }
        boolean selfCollide = snekBody.move(dir);
        if (selfCollide) {
            logger.atInfo().log("Snek bit self and died");
            gameState = GameState.SnekDead;
        }
        if (!board.isValidPos(snekBody.getHead())) {
            logger.atInfo().log("snek fell off map and died");
            gameState = GameState.SnekDead;
        }
        if (snekBody.getHead().equals(board.getApplePos())) {
            snekBody.extendBody(appleIncrease);
            moveApple();
        }

        notifyChange();
    }

    private void moveApple() {
        Optional<IntVector2> newApplePos = RandomUtils.randomizeStream(BoardUtils.boardPositionsAsStream(board).filter(pos -> !snekBody.getBodyPositions().contains(pos))).findFirst();
        if (newApplePos.isPresent()) {
            logger.atInfo().log("Moving apple to %s", newApplePos);
            board.setApplePos(newApplePos.get());
        } else {
            logger.atInfo().log("Unable to determine new location for apple, assuming game is won");
            gameState = GameState.GameWon;
        }
    }

    @Override
    public boolean isComplete() {
        return gameState != GameState.Running;
    }

    public void registerOnChange(Consumer<ReadOnlyBoard<SnekCell>> boardChangeEventListener) {
        boardSubscribers.add(boardChangeEventListener);
    }

    public void notifyChange() {
        for (Consumer<ReadOnlyBoard<SnekCell>> subscriber : boardSubscribers) {
            subscriber.accept(board);
        }
    }

    @Override
    public ReadOnlyBoard<SnekCell> getBoard() {
        return board;
    }
}
