package co.ecso.dacato.database.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Cache interface.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
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
    <V> CompletableFuture<V> get(final CacheKey key, final Callable<CompletableFuture<V>> callback)
            throws ExecutionException;

    /**
     * Add a new entry.
     *
     * @param key   Key.
     * @param value Value.
     */
    <V> void put(final CacheKey key, final CompletableFuture<V> value);

    /**
     * Invalidate an entry.
     *
     * @param key Key to invalidate.
     */
    void invalidate(final CacheKey key);

    /**
     * Invalidate list of keys.
     *
     * @param keys Keys to invalidate.
     */
    void invalidateAll(final Iterable<CacheKey> keys);

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
}
