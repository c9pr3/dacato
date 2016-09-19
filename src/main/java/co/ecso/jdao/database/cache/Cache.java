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

    V get(final K var1, final Callable<? extends V> var2) throws ExecutionException;

    Map<K, V> getAllPresent(final Iterable<?> var1);

    void put(final K var1, final V var2);

    void putAll(final Map<? extends K, ? extends V> var1);

    void invalidate(final Object var1);

    void invalidateAll(final Iterable<?> var1);

    void invalidateAll();

    long size();

    void cleanUp();
}
