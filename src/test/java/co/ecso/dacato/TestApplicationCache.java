package co.ecso.dacato;

import co.ecso.dacato.database.cache.Cache;
import co.ecso.dacato.database.cache.CacheKey;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * TestApplicationCache.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @since 04.09.16
 */
public final class TestApplicationCache implements Cache {

    private static final HazelcastInstance HAZELCAST_INSTANCE = Hazelcast.newHazelcastInstance();
    private static final Map<CacheKey<Object>, Object> APPLICATION_CACHE = HAZELCAST_INSTANCE.getMap("application");

    @Override
    public <V> CompletableFuture<V> get(final CacheKey<Object> cacheKey, final Callable<CompletableFuture<V>> callable)
            throws ExecutionException {
        synchronized (APPLICATION_CACHE) {
            if (!APPLICATION_CACHE.containsKey(cacheKey)) {
                try {
                    final CompletableFuture<V> future = callable.call();
                    //well, we have to "get" here, otherwise we'd be *too* lazy.
                    future.thenApply(toPut -> APPLICATION_CACHE.put(cacheKey, toPut)).get(10, TimeUnit.SECONDS);
                    return future;
                } catch (final Exception e) {
                    throw new ExecutionException(e.getMessage(), e);
                }
            }

            //noinspection unchecked
            return CompletableFuture.completedFuture((V) APPLICATION_CACHE.get(cacheKey));
        }
    }

    @Override
    public <V> void put(final CacheKey<Object> cacheKey, final CompletableFuture<V> value) {
        synchronized (APPLICATION_CACHE) {
            if (!APPLICATION_CACHE.containsKey(cacheKey)) {
                value.thenAccept(toPut -> APPLICATION_CACHE.put(cacheKey, toPut));
            }
        }
    }

    @Override
    public void invalidate(final CacheKey<Object> cacheKey) {
        synchronized (APPLICATION_CACHE) {
            APPLICATION_CACHE.remove(cacheKey);
        }
    }

    @Override
    public Set<CacheKey<Object>> keySet() {
        synchronized (APPLICATION_CACHE) {
            return APPLICATION_CACHE.keySet();
        }
    }

    @Override
    public void invalidateAll() {
        synchronized (APPLICATION_CACHE) {
            APPLICATION_CACHE.clear();
        }
    }

    @Override
    public long size() {
        synchronized (APPLICATION_CACHE) {
            return APPLICATION_CACHE.size();
        }
    }

    @Override
    public void cleanUp() {
        synchronized (APPLICATION_CACHE) {
            APPLICATION_CACHE.clear();
        }
    }

}
