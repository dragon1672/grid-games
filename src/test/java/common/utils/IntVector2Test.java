package common.utils;

import org.junit.Assert;
import org.junit.Test;

public class IntVector2Test {
    @Test
    public void testCreation() {
        IntVector2 vector = IntVector2.of(1, 2);
        Assert.assertEquals(1, vector.x);
        Assert.assertEquals(2, vector.y);
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(IntVector2.of(12, 34), IntVector2.of(12, 34));
    }

    @Test
    public void testAdd() {
        IntVector2 a = IntVector2.of(1, 2);
        IntVector2 b = IntVector2.of(4, 5);

        Assert.assertEquals(IntVector2.of(5, 7), a.add(b));
    }

    @Test
    public void testSub() {
        IntVector2 a = IntVector2.of(1, 2);
        IntVector2 b = IntVector2.of(4, 5);

        Assert.assertEquals(IntVector2.of(-3, -3), a.sub(b));
    }

    @Test
    public void testThatAddIsImmutable() {
        IntVector2 original = IntVector2.of(12, 34);
        original.add(IntVector2.of(56, 78));

        Assert.assertEquals(IntVector2.of(12, 34), original);
    }
}