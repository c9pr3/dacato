package co.ecso.jdao;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * FakeCache.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 25.08.16
 */
public final class FakeCache implements Cache<Object, CompletableFuture<?>> {

    private static final Map<Object, CompletableFuture<?>> CACHE = new HashMap<>();

    @Override
    public CompletableFuture<?> getIfPresent(Object var1) {
        return CACHE.get(var1);
    }

    @Override
    public CompletableFuture<?> get(Object var1, Callable<? extends CompletableFuture<?>> var2) throws ExecutionException {
        if (!CACHE.containsKey(var1)) {
            try {
                CACHE.put(var1, var2.call());
            } catch (Exception e) {
                throw new ExecutionException(e);
            }
        }
        return CACHE.get(var1);
    }

    @Override
    public Map<Object, CompletableFuture<?>> getAllPresent(Iterable<?> var1) {
        return CACHE;
    }

    @Override
    public void put(Object var1, CompletableFuture<?> var2) {
        CACHE.putIfAbsent(var1, var2);
    }

    @Override
    public void putAll(Map<? extends Object, ? extends CompletableFuture<?>> var1) {
        CACHE.putAll(var1);
    }

    @Override
    public void invalidate(Object var1) {
        CACHE.clear();
    }

    @Override
    public void invalidateAll(Iterable<?> var1) {
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
