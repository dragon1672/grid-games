package snek.game;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import common.utils.IntVector2;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

public class MoveableSnekBody implements SnekBody {
    private final LinkedList<IntVector2> bodyChunks = new LinkedList<>();
    private final Multiset<IntVector2> bodyLocations = HashMultiset.create();

    public MoveableSnekBody(IntVector2 startPos, int bodyLength) {
        checkArgument(bodyLength > 0, "body length must be more than one.");
        for (int i = 0; i < bodyLength; i++) {
            bodyChunks.add(startPos);
            bodyLocations.add(startPos);
        }
    }

    @Override
    public void extendBody(int bodyPartsToAdd) {
        for (int i = 0; i < bodyPartsToAdd; i++) {
            bodyChunks.add(bodyChunks.getLast());
            bodyLocations.add(bodyChunks.getLast());
        }
    }

    @Override
    public Set<IntVector2> getBodyPositions() {
        return Collections.unmodifiableSet(bodyLocations.elementSet());
    }

    @Override
    public IntVector2 getHead() {
        return bodyChunks.getFirst();
    }

    public boolean move(Direction direction) {
        IntVector2 newHead = bodyChunks.getFirst().add(direction.vector);
        boolean selfCollide = bodyLocations.contains(newHead);
        bodyChunks.addFirst(newHead);
        bodyLocations.add(newHead);

        IntVector2 oldTail = bodyChunks.removeLast();
        bodyLocations.remove(oldTail);

        return selfCollide;
    }
}
