package common.utils;

import javax.annotation.CheckReturnValue;

/**
 * Immutable 2 dimensional vector of ints
 */
public class IntVector2 {
    public final int x, y;

    private IntVector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @CheckReturnValue
    public static IntVector2 of(int x, int y) {
        return new IntVector2(x, y);
    }

    @CheckReturnValue
    public static IntVector2 iVec(int x, int y) {
        return of(x, y);
    }

    @CheckReturnValue
    public IntVector2 add(IntVector2 that) {
        return of(this.x + that.x, this.y + that.y);
    }

    @CheckReturnValue
    public IntVector2 sub(IntVector2 that) {
        return of(this.x - that.x, this.y - that.y);
    }

    @CheckReturnValue
    public IntVector2 mul(float scale) {
        return of((int) (x * scale), (int) (y * scale));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntVector2 that = (IntVector2) o;

        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return String.format("{%d,%d}", x, y);
    }
}
