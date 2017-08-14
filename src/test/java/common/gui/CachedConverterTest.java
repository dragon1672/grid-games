package common.gui;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

class CachedConverterTest {
    @Test
    void apply() {
        Map<Character, Integer> cacheHits = new HashMap<>();
        CachedConverter<Character, Character> cachedConverter = new CachedConverter<Character, Character>() {
            @Override
            Character doConvert(Character character) {
                cacheHits.put(character, cacheHits.getOrDefault(character, 0) + 1); // increment cache hits
                return character;
            }
        };

        assertThat(cacheHits).doesNotContainKey('A');
        cachedConverter.apply('A');
        assertThat(cacheHits).containsEntry('A', 1);
        cachedConverter.apply('A');
        cachedConverter.apply('A');
        cachedConverter.apply('A');
        assertThat(cacheHits).containsEntry('A', 1);

        assertThat(cacheHits).doesNotContainKey('B');
        cachedConverter.apply('B');
        assertThat(cacheHits).containsEntry('B', 1);
        cachedConverter.apply('B');
        cachedConverter.apply('B');
        cachedConverter.apply('B');
        assertThat(cacheHits).containsEntry('B', 1);
    }

}