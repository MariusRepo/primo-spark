package org.primo.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SpinCache {
    private static final SpinCache INSTANCE = new SpinCache();
    private final ConcurrentMap<String, CachedSpin> cache = new ConcurrentHashMap<>();

    private SpinCache() {
    }

    public static SpinCache getCache() {
        return INSTANCE;
    }

    public void put(String spinToken, CachedSpin cachedSpin) {
        cache.put(spinToken, cachedSpin);
    }

    public CachedSpin get(String spinToken) {
        return cache.get(spinToken);
    }

    public void remove(String spinToken) {
        cache.remove(spinToken);
    }
}
