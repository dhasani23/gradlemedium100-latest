package com.gradlemedium100.common.util;

import org.springframework.stereotype.Component;
import org.springframework.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Utility class that provides a wrapper around Spring's CacheManager
 * to simplify cache operations throughout the application.
 */
@Component
public class CacheManager {

    private final org.springframework.cache.CacheManager springCacheManager;

    /**
     * Constructor that injects Spring's CacheManager
     *
     * @param springCacheManager the Spring CacheManager to use
     */
    @Autowired
    public CacheManager(org.springframework.cache.CacheManager springCacheManager) {
        this.springCacheManager = springCacheManager;
    }

    /**
     * Get a value from the cache
     *
     * @param key the cache key
     * @return the cached value, or null if not found
     */
    public Object get(String key) {
        Cache cache = springCacheManager.getCache("default");
        if (cache != null) {
            Cache.ValueWrapper valueWrapper = cache.get(key);
            return valueWrapper != null ? valueWrapper.get() : null;
        }
        return null;
    }

    /**
     * Store a value in the cache
     *
     * @param key the cache key
     * @param value the value to cache
     */
    public void put(String key, Object value) {
        Cache cache = springCacheManager.getCache("default");
        if (cache != null) {
            cache.put(key, value);
        }
    }

    /**
     * Remove a value from the cache
     *
     * @param key the cache key to remove
     */
    public void remove(String key) {
        Cache cache = springCacheManager.getCache("default");
        if (cache != null) {
            cache.evict(key);
        }
    }

    /**
     * Clear all entries from the cache
     */
    public void clear() {
        Cache cache = springCacheManager.getCache("default");
        if (cache != null) {
            cache.clear();
        }
    }
}