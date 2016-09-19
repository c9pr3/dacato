package co.ecso.jdao;

import co.ecso.jdao.database.cache.Cache;
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
public final class TestApplicationCache<K, V> implements Cache<K, V> {
    private final com.google.common.cache.Cache<K, V>
            CACHE = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).maximumSize(50).build();

    @Override
    public V getIfPresent(final K key) {
        return CACHE.getIfPresent(key);
    }

    @Override
    public V get(final K var1, final Callable<? extends V> var2) throws ExecutionException {
        return CACHE.get(var1, var2);
    }

    @Override
    public Map<K, V> getAllPresent(final Iterable<?> var1) {
        return CACHE.getAllPresent(var1);
    }

    @Override
    public void put(final K var1, final V var2) {
        CACHE.put(var1, var2);
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> var1) {
        CACHE.putAll(var1);
    }

    @Override
    public void invalidate(final Object var1) {
        CACHE.invalidate(var1);
    }

    @Override
    public void invalidateAll(final Iterable<?> var1) {
        CACHE.invalidateAll(var1);
    }

    @Override
    public void invalidateAll() {
        CACHE.invalidateAll();
    }

    @Override
    public long size() {
        return CACHE.size();
    }

    @Override
    public void cleanUp() {
        CACHE.cleanUp();
    }
}
