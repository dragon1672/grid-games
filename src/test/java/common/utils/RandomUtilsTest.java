package common.utils;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RandomUtilsTest {

    @Before
    public void setup() {
        RandomUtils.rand = new Random(42);
    }

    @Test
    void dumbCoverageTest() {
        new RandomUtils();
    }

    @Test
    void randomSubset_sublistToLarge() {
        assertThrows(IllegalArgumentException.class, () -> RandomUtils.randomSubset(ImmutableList.of(), 100));
    }

    @Test
    void randomSubset_emptyListEmptySublist() {
        assertThat(RandomUtils.randomSubset(ImmutableList.of(), 0)).isEmpty();
    }

    @Test
    void randomSubset() {
        List<Integer> listOfElements = ImmutableList.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0);
        List<Integer> randomSubList = RandomUtils.randomSubset(listOfElements, 5);
        assertThat(randomSubList).containsNoDuplicates();
        assertThat(listOfElements).containsAllIn(randomSubList);
    }

    @Test
    void randomFromList_emptyList() {
        assertThrows(IllegalArgumentException.class, () -> RandomUtils.randomFromList(ImmutableList.of()));
    }

    @Test
    void randomFromList_oneElementList() {
        assertThat(RandomUtils.randomFromList(ImmutableList.of(1))).isEqualTo(1);
    }

    @Test
    void randomFromList() {
        List<Integer> list = ImmutableList.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0);
        Integer randomVal = RandomUtils.randomFromList(list);
        assertThat(randomVal).isIn(list);
    }
}