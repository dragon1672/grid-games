package common.utils;

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class IntVector2Test {
    @Test
    void testCreation() {
        IntVector2 vector = IntVector2.of(1, 2);
        assertThat(vector.x).isEqualTo(1);
        assertThat(vector.y).isEqualTo(2);
    }

    @Test
    void testEquals() {
        assertThat(IntVector2.of(12, 34)).isEqualTo(IntVector2.of(12, 34));
    }

    @Test
    void testAdd() {
        IntVector2 a = IntVector2.of(1, 2);
        IntVector2 b = IntVector2.of(4, 5);

        assertThat(a.add(b)).isEqualTo(IntVector2.of(5, 7));
    }

    @Test
    void testSub() {
        IntVector2 a = IntVector2.of(1, 2);
        IntVector2 b = IntVector2.of(4, 5);

        assertThat(a.sub(b)).isEqualTo(IntVector2.of(-3, -3));
    }

    @Test
    void testMul() {
        IntVector2 a = IntVector2.of(1, 2);
        float scale = 2;

        assertThat(a.mul(scale)).isEqualTo(IntVector2.of(2, 4));
    }

    @Test
    void testMulNegative() {
        IntVector2 a = IntVector2.of(1, 2);
        float scale = -2;

        assertThat(a.mul(scale)).isEqualTo(IntVector2.of(-2, -4));
    }

    @Test
    void testThatAddIsImmutable() {
        IntVector2 original = IntVector2.of(12, 34);
        original.add(IntVector2.of(56, 78));

        assertThat(original).isEqualTo(IntVector2.of(12, 34));
    }

    @Test
    void testToString() {
        assertThat(IntVector2.of(42, 0).toString()).contains("42");
        assertThat(IntVector2.of(0, 42).toString()).contains("42");
    }
}