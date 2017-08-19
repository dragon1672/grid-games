package common.utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SetOperations {

    @VisibleForTesting
    SetOperations() {
    }

    /**
     * Calculates power set with recursion
     *
     * @param originalSet set to determine power set of
     */
    static <T> Set<Set<T>> powerSet_Recursive(Set<T> originalSet) {
        Set<Set<T>> powerSets = new HashSet<>();
        powerSets.add(originalSet);
        for (T element : originalSet) {
            Set<T> elementAsSet = ImmutableSet.of(element);
            Set<T> setMinusElement = Sets.difference(originalSet, elementAsSet);
            powerSets.addAll(powerSet_Recursive(setMinusElement));
        }
        return powerSets;
    }

    /**
     * Calculates power set without recursion
     *
     * @param originalSet set to determine power set of
     */
    static <T> Set<Set<T>> powerSet_iterative(Set<T> originalSet) {
        Set<Set<T>> powerSets = new HashSet<>();
        Stack<Set<T>> elementsToProcess = new Stack<>();
        elementsToProcess.add(originalSet);

        while (!elementsToProcess.isEmpty()) {
            Set<T> set = elementsToProcess.pop();
            powerSets.add(set);
            for (T element : set) {
                Set<T> elementAsSet = ImmutableSet.of(element);
                Set<T> setMinusElement = Sets.difference(set, elementAsSet);
                elementsToProcess.add(setMinusElement);
            }
        }
        return powerSets;
    }

    /**
     * Calculates the power set by "incrementing" a binary array like it was a binary number to determine every possible combination.
     *
     * Taking a 4 byte example
     * 0000 = {}
     * 0001 = {4}
     * 0010 = {3}
     * 0011 = {3,4}
     * 0100 = {2}
     * 0101 = {2,4}
     * 0111 = {2,3,4}
     * 1000 = {1}
     * 1001 = {1,4}
     * 1010 = {1,3}
     * 1011 = {1,3,4}
     * 1100 = {1,2}
     * 1101 = {1,2,4}
     * 1111 = {1,2,3,4}
     *
     * @param originalSet set to determine power set of
     */
    static <T> Set<Set<T>> powerSet_bitSet(Set<T> originalSet) {
        @SuppressWarnings("unchecked") T[] array = (T[]) originalSet.toArray();
        // setting to plus 1 so I can query if the last element has toggled to determine if we have gone over all possibilities
        BitSet demBites = new BitSet(array.length + 1);
        Set<Set<T>> powerSets = new HashSet<>();

        powerSets.add(ImmutableSet.of()); // ensure with array length 0 empty set is included

        while (!demBites.get(array.length)) {
            Set<T> subSet = new HashSet<>();
            for (int i = 0; i < array.length; i++) {
                if (demBites.get(i)) {
                    subSet.add(array[i]);
                }
            }
            powerSets.add(subSet);
            // now to update dem bites
            for (int i = 0; i < demBites.size(); i++) {
                if (!demBites.get(i)) {
                    demBites.set(i); // set to true
                    break;
                } else {
                    demBites.clear(i); // set to false
                }
            }
        }
        return powerSets;
    }

    /**
     * Calculates the power set by "incrementing" a binary array like it was a binary number to determine every possible combination.
     * Calculated lazily as stream iterates
     *
     * @param originalSet set to find power set for
     */
    static <T> Stream<Set<T>> stream_powerSet_bitSet(Set<T> originalSet) {
        @SuppressWarnings("unchecked") T[] array = (T[]) originalSet.toArray();
        // Adding plus + to allow empty set at the end
        BitSet demBites = new BitSet(array.length + 1);

        long powerSetLength = (long) Math.pow(2, originalSet.size());

        return Stream.generate(() -> {
            Set<T> subSet = new HashSet<>();
            for (int i = 0; i < array.length; i++) {
                if (demBites.get(i)) {
                    subSet.add(array[i]);
                }
            }
            // now to update dem bites
            for (int i = 0; i < demBites.size(); i++) {
                if (!demBites.get(i)) {
                    demBites.set(i); // set to true
                    break;
                } else {
                    demBites.clear(i); // set to false
                }
            }
            return subSet;
        }).limit(powerSetLength
        );
    }

    /**
     * Get power set of any size array lazily calculated as a stream
     *
     * @param originalSet set to find power set for
     */
    public static <T> Stream<Set<T>> powerSet(Set<T> originalSet) {
        return stream_powerSet_bitSet(originalSet);
    }

    @SafeVarargs
    public static <T> Predicate<T> elementIsIn(T... possibleTargets) {
        return ImmutableSet.copyOf(possibleTargets)::contains;
    }
}
