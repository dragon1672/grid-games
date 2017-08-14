package common.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class CachedConverter<A,B> implements Function<A, B> {

    private final Map<A,B> cache = new HashMap<>();

    abstract B doConvert(A a);

    @Override
    public B apply(A a) {
        return cache.computeIfAbsent(a, this::doConvert);
    }
}
