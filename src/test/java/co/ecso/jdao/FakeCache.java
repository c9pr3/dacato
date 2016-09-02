package co.ecso.jdao;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

/**
 * FakeCache.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 25.08.16
 */
final class FakeCache implements Cache<CachingConnectionWrapper.CacheKey<?>, CompletableFuture<?>> {

    private static final Map<CachingConnectionWrapper.CacheKey<?>, CompletableFuture<?>> CACHE = new HashMap<>();

    @Override
    public CompletableFuture<?> getIfPresent(final Object var1) {
        //noinspection RedundantCast
        return CACHE.get((CachingConnectionWrapper.CacheKey<?>) var1);
    }

    @Override
    public CompletableFuture<?> get(final CachingConnectionWrapper.CacheKey<?> var1,
                                    final Callable<? extends CompletableFuture<?>> var2) {
        if (!CACHE.containsKey(var1)) {
            try {
                CACHE.put(var1, var2.call());
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return CACHE.get(var1);
    }

    @Override
    public Map<CachingConnectionWrapper.CacheKey<?>, CompletableFuture<?>> getAllPresent(final Iterable<?> var1) {
        return CACHE;
    }

    @Override
    public void put(final CachingConnectionWrapper.CacheKey<?> var1, final CompletableFuture<?> var2) {
        CACHE.putIfAbsent(var1, var2);
    }

    @Override
    public void putAll(final Map<? extends CachingConnectionWrapper.CacheKey<?>, ? extends CompletableFuture<?>> var1) {

    }

    @Override
    public void invalidate(final Object var1) {
        CACHE.clear();
    }

    @Override
    public void invalidateAll(final Iterable<?> var1) {
        CACHE.clear();
    }

    @Override
    public void invalidateAll() {
        CACHE.clear();
    }

    @Override
    public long size() {
        return (long) CACHE.size();
    }

    @Override
    public void cleanUp() {
        CACHE.clear();
    }
}
