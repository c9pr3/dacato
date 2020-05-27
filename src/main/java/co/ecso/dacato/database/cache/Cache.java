package co.ecso.dacato.database.cache;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Cache interface.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @since 25.08.16
 */
public interface Cache {

    /**
     * Get entry.
     *
     * @param key      Key to getRealFuture.
     * @param callback Callback to call when key is not yet in cache.
     * @return Value.
     * @throws ExecutionException if callback fails.
     */
    <V> CompletableFuture<V> get(final CacheKey<Object> key, final Callable<CompletableFuture<V>> callback)
            throws ExecutionException;

    /**
     * Add a new entry.
     *
     * @param key   Key.
     * @param value Value.
     */
    <V> void put(final CacheKey<Object> key, final CompletableFuture<V> value);

    /**
     * Invalidate one entry.
     *
     * @param cacheKey Cache key to invalidate.
     */
    void invalidate(CacheKey<Object> cacheKey);

    /**
     * Invalidate all entries.
     */
    void invalidateAll();

    /**
     * Number of entries.
     *
     * @return Number of entries..
     */
    long size();

    /**
     * Cleanup invalidated entries.
     */
    void cleanUp();

    /**
     * Get key set.
     *
     * @return List of Cache keys.
     */
    Set<CacheKey<Object>> keySet();

}
