package co.ecso.dacato;

import co.ecso.dacato.database.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * TestApplicationCache.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 04.09.16
 */
@SuppressWarnings("WeakerAccess")
public final class TestApplicationCache<K, V> implements Cache<K, V> {
    private final com.google.common.cache.Cache<K, V>
            kvCache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).maximumSize(50).build();

    @Override
    public V getIfPresent(final K key) {
        return kvCache.getIfPresent(key);
    }

    @Override
    public V get(final K key, final Callable<? extends V> callback) throws ExecutionException {
        return kvCache.get(key, callback);
    }

    @Override
    public Map<K, V> getAllPresent(final Iterable<?> key) {
        return kvCache.getAllPresent(key);
    }

    @Override
    public void put(final K key, final V value) {
        kvCache.put(key, value);
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> keyValues) {
        kvCache.putAll(keyValues);
    }

    @Override
    public void invalidate(final K key) {
        kvCache.invalidate(key);
    }

    @Override
    public void invalidateAll(final Iterable<K> keys) {
        kvCache.invalidateAll(keys);
    }

    @Override
    public void invalidateAll() {
        kvCache.invalidateAll();
    }

    @Override
    public long size() {
        return kvCache.size();
    }

    @Override
    public void cleanUp() {
        kvCache.cleanUp();
    }
}
