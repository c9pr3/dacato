package co.ecso.dacato;

import co.ecso.dacato.database.cache.Cache;
import co.ecso.dacato.database.cache.CacheKey;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * TestApplicationCache.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 04.09.16
 */
public final class TestApplicationCache implements Cache {

    private static final HazelcastInstance HAZELCAST_INSTANCE = Hazelcast.newHazelcastInstance();
    private static final Map<Object, Object> APPLICATION_CACHE = HAZELCAST_INSTANCE.getMap("application");

    @Override
    public <V> CompletableFuture<V> get(final CacheKey key, final Callable<CompletableFuture<V>> callable)
            throws ExecutionException {
        if (!APPLICATION_CACHE.containsKey(key)) {
            try {
                final CompletableFuture<V> future = callable.call();
                //well, we have to "get" here, otherwise we'd be *too* lazy.
                //@TODO add timeout
                future.thenApply(toPut -> APPLICATION_CACHE.put(key, toPut)).get();
                return future;
            } catch (final Exception e) {
                throw new ExecutionException(e.getMessage(), e);
            }
        }

        return CompletableFuture.completedFuture((V) APPLICATION_CACHE.get(key));
    }

    @Override
    public <V> void put(final CacheKey key, final CompletableFuture<V> value) {
        if (!APPLICATION_CACHE.containsKey(key)) {
            value.thenAccept(toPut -> APPLICATION_CACHE.put(key, toPut));
        }
    }

    @Override
    public void invalidateAll(final Iterable<CacheKey> keys) {
        keys.forEach(APPLICATION_CACHE::remove);
    }

    @Override
    public void invalidate(final CacheKey var1) {
        APPLICATION_CACHE.remove(var1);
    }

    @Override
    public void invalidateAll() {
        APPLICATION_CACHE.clear();
    }

    @Override
    public long size() {
        return APPLICATION_CACHE.size();
    }

    @Override
    public void cleanUp() {
        APPLICATION_CACHE.clear();
    }

}
