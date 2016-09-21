package co.ecso.jdao.database.cache;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Cache interface.
 *
 * @param <K> Key Type.
 * @param <V> Value Type.
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 25.08.16
 */
@SuppressWarnings("unused")
public interface Cache<K, V> {
    /**
     * Get entry if present.
     *
     * @param key Key to get.
     * @return Value.
     */
    V getIfPresent(final K key);

    /**
     * Get entry.
     *
     * @param key Key to get.
     * @param callback Callback to call when key is not yet in cache.
     * @return Value.
     * @throws ExecutionException if callback fails.
     */
    V get(final K key, final Callable<? extends V> callback) throws ExecutionException;

    /**
     * Get all entries which currently exist.
     *
     * @param key Key.
     * @return Values.
     */
    Map<K, V> getAllPresent(final Iterable<?> key);

    /**
     * Add a new entry.
     *
     * @param key Key.
     * @param value Value.
     */
    void put(final K key, final V value);

    /**
     * Add all from this map..
     *
     * @param keyValues Key columnValuesToSet to add.
     */
    void putAll(final Map<? extends K, ? extends V> keyValues);

    /**
     * Invalidate an entry.
     *
     * @param key Key to invalidate.
     */
    void invalidate(final K key);

    /**
     * Invalidate list of keys.
     *
     * @param keys Keys to invalidate.
     */
    void invalidateAll(final Iterable<K> keys);

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
