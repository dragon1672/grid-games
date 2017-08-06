package common.utils;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.fail;

public class SetOperationsTest {
    private static <T> void assertEqualPowerSets(Set<Set<T>> output, Set<Set<T>> expected) {
        Set<Set<T>> toCheck = new HashSet<>(output);
        Set<Set<T>> missing = new HashSet<>();
        for (Set<T> expectedSet : expected) {
            boolean foundMatch = toCheck.removeIf(toCheckSet -> toCheckSet.containsAll(expectedSet) && expectedSet.containsAll(toCheckSet));
            if (!foundMatch) {
                missing.add(expectedSet);
            }
        }
        StringBuilder errorMessages = new StringBuilder();
        if (!missing.isEmpty()) {
            String asStr = missing.stream().map(collection -> Arrays.toString(missing.toArray())).collect(Collectors.joining(", \n\t"));
            errorMessages.append(String.format("Missing Elements: {\n\t%s\n}\n", asStr));
        }
        if (!toCheck.isEmpty()) {
            String asStr = toCheck.stream().map(collection -> Arrays.toString(missing.toArray())).collect(Collectors.joining(", \n\t"));
            errorMessages.append(String.format("Extra elements: {\n\t%s\n}\n", asStr));
        }
        Assert.assertTrue(errorMessages.toString(), missing.isEmpty() && toCheck.isEmpty());
    }

    private static <T> void testPowerSetStream(Function<Set<T>, Stream<Set<T>>> function, Set<T> input) {
        Set<Set<T>> expectedOutput = Sets.powerSet(input);
        Set<Set<T>> output = function.apply(input).collect(Collectors.toSet());
        assertEqualPowerSets(output, expectedOutput);
    }

    private static <T> void testPowerSet(Function<Set<T>, Set<Set<T>>> function, Set<T> input) {
        Set<Set<T>> expectedOutput = Sets.powerSet(input);
        Set<Set<T>> output = function.apply(input);
        assertEqualPowerSets(output, expectedOutput);
    }

    @Test
    public void testMyPowerEquals() throws Exception {
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
    public void testMyPowerEquals_missingElement() throws Exception {
        try {
            assertEqualPowerSets(
                    ImmutableSet.of(
                            ImmutableSet.of(1),
                            ImmutableSet.of(1, 2)
                    ),
                    ImmutableSet.of(
                            ImmutableSet.of(1),
                            ImmutableSet.of(1, 2),
                            ImmutableSet.of(1, 2, 3)
                    )
            );
            fail();
        } catch (AssertionError ignored) {
        }
    }

    @Test
    public void testMyPowerEquals_ExtraElement() throws Exception {
        try {
            assertEqualPowerSets(
                    ImmutableSet.of(
                            ImmutableSet.of(1),
                            ImmutableSet.of(1, 2),
                            ImmutableSet.of(1, 2, 3)
                    ),
                    ImmutableSet.of(
                            ImmutableSet.of(1),
                            ImmutableSet.of(1, 2)
                    )
            );
            fail();
        } catch (AssertionError ignored) {
        }
    }

    @Test
    public void powerSet_Recursive_0() throws Exception {
        testPowerSet(SetOperations::powerSet_Recursive, ImmutableSet.of());
    }

    @Test
    public void powerSet_Recursive_1() throws Exception {
        testPowerSet(SetOperations::powerSet_Recursive, ImmutableSet.of(1));
    }

    @Test
    public void powerSet_Recursive_2() throws Exception {
        testPowerSet(SetOperations::powerSet_Recursive, ImmutableSet.of(1, 2));
    }

    @Test
    public void powerSet_Recursive_3() throws Exception {
        testPowerSet(SetOperations::powerSet_Recursive, ImmutableSet.of(1, 2, 3));
    }

    @Test
    public void powerSet_Recursive_4() throws Exception {
        testPowerSet(SetOperations::powerSet_Recursive, ImmutableSet.of(1, 2, 3, 4));
    }

    @Test
    public void powerSet_iterative_0() throws Exception {
        testPowerSet(SetOperations::powerSet_iterative, ImmutableSet.of());
    }

    @Test
    public void powerSet_iterative_1() throws Exception {
        testPowerSet(SetOperations::powerSet_iterative, ImmutableSet.of(1));
    }

    @Test
    public void powerSet_iterative_2() throws Exception {
        testPowerSet(SetOperations::powerSet_iterative, ImmutableSet.of(1, 2));
    }

    @Test
    public void powerSet_iterative_3() throws Exception {
        testPowerSet(SetOperations::powerSet_iterative, ImmutableSet.of(1, 2, 3));
    }

    @Test
    public void powerSet_iterative_4() throws Exception {
        testPowerSet(SetOperations::powerSet_iterative, ImmutableSet.of(1, 2, 3, 4));
    }

    @Test
    public void powerSet_bitSet_0() throws Exception {
        testPowerSet(SetOperations::powerSet_bitSet, ImmutableSet.of());
    }

    @Test
    public void powerSet_bitSet_1() throws Exception {
        testPowerSet(SetOperations::powerSet_bitSet, ImmutableSet.of(1));
    }

    @Test
    public void powerSet_bitSet_2() throws Exception {
        testPowerSet(SetOperations::powerSet_bitSet, ImmutableSet.of(1, 2));
    }

    @Test
    public void powerSet_bitSet_3() throws Exception {
        testPowerSet(SetOperations::powerSet_bitSet, ImmutableSet.of(1, 2, 3));
    }

    @Test
    public void powerSet_bitSet_4() throws Exception {
        testPowerSet(SetOperations::powerSet_bitSet, ImmutableSet.of(1, 2, 3, 4));
    }

    @Test
    public void stream_powerSet_bitSet_0() throws Exception {
        testPowerSetStream(SetOperations::stream_powerSet_bitSet, ImmutableSet.of());
    }

    @Test
    public void stream_powerSet_bitSet_1() throws Exception {
        testPowerSetStream(SetOperations::stream_powerSet_bitSet, ImmutableSet.of(1));
    }

    @Test
    public void stream_powerSet_bitSet_2() throws Exception {
        testPowerSetStream(SetOperations::stream_powerSet_bitSet, ImmutableSet.of(1, 2));
    }

    @Test
    public void stream_powerSet_bitSet_3() throws Exception {
        testPowerSetStream(SetOperations::stream_powerSet_bitSet, ImmutableSet.of(1, 2, 3));
    }

    @Test
    public void stream_powerSet_bitSet_4() throws Exception {
        testPowerSetStream(SetOperations::stream_powerSet_bitSet, ImmutableSet.of(1, 2, 3, 4));
    }
}