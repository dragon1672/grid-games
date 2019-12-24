package snek.game;

import common.utils.IntVector2;

import java.util.Set;

public interface SnekBody {
    void extendBody(int bodyPartsToAdd);

    Set<IntVector2> getBodyPositions();

    IntVector2 getHead();
}
