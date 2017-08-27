package common.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class SetOperationsTest {
    private static <T> void assertEqualPowerSets(Set<Set<T>> output, Set<Set<T>> expected) {
        Set<Set<T>> toCheck = new HashSet<>(output);
        Set<Set<T>> missing = new HashSet<>();
        for (Set<T> expectedSet : expected) {
            boolean foundMatch = toCheck.removeIf(toCheckSet -> toCheckSet.containsAll(expectedSet) && expectedSet.containsAll(toCheckSet));
            if (!foundMatch) {
                missing.add(expectedSet);
            }
        }
        assertThat(missing).named("missing elements").isEmpty();
        assertThat(toCheck).named("extra elements").isEmpty();
    }

    private static <T> DynamicTest powerSetStreamTest(Function<Set<T>, Stream<Set<T>>> function, Set<T> input) {
        return dynamicTest(
                input.size() + " elements",
                () -> {
                    Set<Set<T>> expectedOutput = Sets.powerSet(input);
                    Set<Set<T>> output = function.apply(input).collect(Collectors.toSet());
                    assertEqualPowerSets(output, expectedOutput);
                });
    }

    private static <T> DynamicTest powerSetTest(Function<Set<T>, Set<Set<T>>> function, Set<T> input) {
        return dynamicTest(
                input.size() + " elements",
                () -> {
                    Set<Set<T>> expectedOutput = Sets.powerSet(input);
                    Set<Set<T>> output = function.apply(input);
                    assertEqualPowerSets(output, expectedOutput);
                }
        );
    }

    @Test
    void testMyPowerEquals() throws Exception {
        assertEqualPowerSets(
                ImmutableSet.of(
                        ImmutableSet.of(1),
                        ImmutableSet.of(1, 2),
                        ImmutableSet.of(1, 2, 3)
                ),
                ImmutableSet.of(
                        ImmutableSet.of(1),
                        ImmutableSet.of(1, 2),
                        ImmutableSet.of(1, 2, 3)
                )
        );
        assertEqualPowerSets(
                ImmutableSet.of(
                        ImmutableSet.of(1)
                ),
                ImmutableSet.of(
                        ImmutableSet.of(1)
                )
        );
    }

    @Test
    void testMyPowerEquals_missingElement() throws Exception {
        assertThrows(AssertionError.class, () -> assertEqualPowerSets(
                ImmutableSet.of(
                        ImmutableSet.of(1),
                        ImmutableSet.of(1, 2)
                ),
                ImmutableSet.of(
                        ImmutableSet.of(1),
                        ImmutableSet.of(1, 2),
                        ImmutableSet.of(1, 2, 3)
                )
        ));
    }

    @Test
    void testMyPowerEquals_ExtraElement() throws Exception {
        assertThrows(AssertionError.class, () -> assertEqualPowerSets(
                ImmutableSet.of(
                        ImmutableSet.of(1),
                        ImmutableSet.of(1, 2),
                        ImmutableSet.of(1, 2, 3)
                ),
                ImmutableSet.of(
                        ImmutableSet.of(1),
                        ImmutableSet.of(1, 2)
                )
        ));
    }

    @TestFactory
    List<DynamicTest> powerSet_Recursive() {
        return ImmutableList.of(
                powerSetTest(SetOperations::powerSet_Recursive, ImmutableSet.of()),
                powerSetTest(SetOperations::powerSet_Recursive, ImmutableSet.of(1)),
                powerSetTest(SetOperations::powerSet_Recursive, ImmutableSet.of(1, 2)),
                powerSetTest(SetOperations::powerSet_Recursive, ImmutableSet.of(1, 2, 3)),
                powerSetTest(SetOperations::powerSet_Recursive, ImmutableSet.of(1, 2, 3, 4))
        );
    }

    @TestFactory
    List<DynamicTest> powerSet_iterative() {
        return ImmutableList.of(
                powerSetTest(SetOperations::powerSet_iterative, ImmutableSet.of()),
                powerSetTest(SetOperations::powerSet_iterative, ImmutableSet.of(1)),
                powerSetTest(SetOperations::powerSet_iterative, ImmutableSet.of(1, 2)),
                powerSetTest(SetOperations::powerSet_iterative, ImmutableSet.of(1, 2, 3)),
                powerSetTest(SetOperations::powerSet_iterative, ImmutableSet.of(1, 2, 3, 4))
        );
    }

    @TestFactory
    List<DynamicTest> powerSet_bitSet() {
        return ImmutableList.of(
                powerSetTest(SetOperations::powerSet_bitSet, ImmutableSet.of()),
                powerSetTest(SetOperations::powerSet_bitSet, ImmutableSet.of(1)),
                powerSetTest(SetOperations::powerSet_bitSet, ImmutableSet.of(1, 2)),
                powerSetTest(SetOperations::powerSet_bitSet, ImmutableSet.of(1, 2, 3)),
                powerSetTest(SetOperations::powerSet_bitSet, ImmutableSet.of(1, 2, 3, 4))
        );
    }

    @TestFactory
    List<DynamicTest> stream_powerSet_bitSet() {
        return ImmutableList.of(
                powerSetStreamTest(SetOperations::stream_powerSet_bitSet, ImmutableSet.of()),
                powerSetStreamTest(SetOperations::stream_powerSet_bitSet, ImmutableSet.of(1)),
                powerSetStreamTest(SetOperations::stream_powerSet_bitSet, ImmutableSet.of(1, 2)),
                powerSetStreamTest(SetOperations::stream_powerSet_bitSet, ImmutableSet.of(1, 2, 3)),
                powerSetStreamTest(SetOperations::stream_powerSet_bitSet, ImmutableSet.of(1, 2, 3, 4))
        );
    }

    @TestFactory
    List<DynamicTest> stream_powerSet() {
        return ImmutableList.of(
                powerSetStreamTest(SetOperations::powerSet, ImmutableSet.of()),
                powerSetStreamTest(SetOperations::powerSet, ImmutableSet.of(1)),
                powerSetStreamTest(SetOperations::powerSet, ImmutableSet.of(1, 2)),
                powerSetStreamTest(SetOperations::powerSet, ImmutableSet.of(1, 2, 3)),
                powerSetStreamTest(SetOperations::powerSet, ImmutableSet.of(1, 2, 3, 4))
        );
    }

    @TestFactory
    List<DynamicTest> elementIsIn() {
        return ImmutableList.of(
                dynamicTest(" 1", () -> assertThat(SetOperations.elementIsIn(1, 2, 3).test(1)).isTrue()),
                dynamicTest(" 2", () -> assertThat(SetOperations.elementIsIn(1, 2, 3).test(2)).isTrue()),
                dynamicTest(" 3", () -> assertThat(SetOperations.elementIsIn(1, 2, 3).test(3)).isTrue()),
                dynamicTest(" 4", () -> assertThat(SetOperations.elementIsIn(1, 2, 3).test(4)).isFalse()),
                dynamicTest("-1", () -> assertThat(SetOperations.elementIsIn(1, 2, 3).test(-1)).isFalse())
        );
    }
}