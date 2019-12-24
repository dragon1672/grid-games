package snek.ai;

import com.google.common.collect.ImmutableList;
import common.board.ReadOnlyBoard;
import common.utils.Flogger;
import common.utils.IntVector2;
import snek.game.Direction;
import snek.game.SnekBody;
import snek.game.SnekCell;
import snek.game.SnekGame;

import javax.annotation.Nullable;
import java.util.*;

public class AStarySnekPlayer implements SnekPlayer {
    private static final Flogger logger = Flogger.getInstance();

    private static final int DELAY = 100;
    private static final ImmutableList<Direction> DIRECTIONS = ImmutableList.of(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT);

    private static class MoveNode {
        public final MoveNode from;
        public final IntVector2 pos;
        public final Direction direction;
        public final int weight;

        private MoveNode(MoveNode from, IntVector2 pos, Direction direction, int weight) {
            this.from = from;
            this.pos = pos;
            this.direction = direction;
            this.weight = weight;
        }

        public static MoveNode create(@Nullable MoveNode from, IntVector2 pos, Direction direction, IntVector2 goal) {
            int parentWeight = from == null ? 0 : from.weight;
            return new MoveNode(
                    from,
                    pos,
                    direction,
                    parentWeight + 1
            );
        }

        public int heuristic(IntVector2 destination) {
            return weight + pos.sub(destination).lengthSquared();
        }
    }

    private LinkedList<Direction> moveToApple(SnekBody snek, ReadOnlyBoard<SnekCell> board, IntVector2 applePos) {
        PriorityQueue<MoveNode> moves = new PriorityQueue<>(Comparator.comparingInt(o -> o.heuristic(applePos)));
        Map<IntVector2, MoveNode> moveLookup = new HashMap<>();

        MoveNode firstMove = MoveNode.create(null, snek.getHead(), null, applePos);
        moves.add(firstMove);
        moveLookup.put(snek.getHead(), firstMove);

        while (!moves.isEmpty()) {
            MoveNode moveToEval = moves.poll();
            if (moveToEval.pos.equals(applePos)) {
                return generatePath(moveToEval);
            }
            for (Direction direction : DIRECTIONS) {
                IntVector2 pos = moveToEval.pos.add(direction.vector);
                if (board.isValidPos(pos)
                        && !snek.getBodyPositions().contains(pos)) {
                    MoveNode newNode = MoveNode.create(
                            moveToEval,
                            pos,
                            direction,
                            applePos);
                    if (moveLookup.containsKey(pos)) {
                        MoveNode existing = moveLookup.get(pos);
                        if (existing.weight <= newNode.weight) {
                            continue; // existing connection faster
                        } else {
                            moves.remove(existing);
                            moveLookup.remove(pos);
                        }
                    }
                    moves.add(newNode);
                    moveLookup.put(pos, newNode);
                }
            }
        }

        throw new RuntimeException("Couldn't find a valid path...sorry");
    }

    private LinkedList<Direction> generatePath(MoveNode endMove) {
        MoveNode node = endMove;
        LinkedList<Direction> directions = new LinkedList<>();
        while (node != null) {
            if (node.direction != null) {
                directions.push(node.direction);
            }
            node = node.from;
        }
        return directions;
    }

    private void pauseForHumans() {
        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            logger.atError().log(e.getMessage());
        }
    }

    @Override
    public void play(SnekGame game) {
        while (!game.isComplete()) {
            LinkedList<Direction> moves = moveToApple(game.getSnek(), game.getBoard(), game.getApplePos());

            while (!moves.isEmpty()) {
                game.move(moves.pollFirst());
                pauseForHumans();
            }
        }
    }
}
