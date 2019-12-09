package conway_lyfe.game;

import com.google.common.collect.Iterables;
import common.utils.IntVector2;

public interface ConwayReadOnlyBoard {
    boolean isActive(IntVector2 pos);

    Iterable<IntVector2> getPoints();

    default String asString() {
        if (Iterables.isEmpty(getPoints())) {
            return "";
        }
        IntVector2 firstPoint = Iterables.getFirst(getPoints(), null);
        assert firstPoint != null;
        int lowX = firstPoint.x;
        int lowY = firstPoint.y;
        int highX = firstPoint.x;
        int highY = firstPoint.y;

        for (IntVector2 point : getPoints()) {
            lowX = Math.min(lowX, point.x);
            lowY = Math.min(lowY, point.y);
            highX = Math.max(highX, point.x);
            highY = Math.max(highY, point.y);
        }
        StringBuilder str = new StringBuilder();

        for (int y = highY; y >= lowY; y--) {
            for (int x = lowX; x <= highX; x++) {
                IntVector2 pos = IntVector2.iVec(x, y);
                char c = isActive(pos) ? 'X' : ' ';
                str.append(c);
            }
            str.append('\n');
        }
        return str.toString();
    }
}
