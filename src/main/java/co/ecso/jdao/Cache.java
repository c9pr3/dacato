package co.ecso.jdao;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Cache interface.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 25.08.16
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public interface Cache<K, V> {
    V getIfPresent(final Object var1);

    V get(final K var1, final Callable<? extends V> var2);

    Map<K, V> getAllPresent(final Iterable<?> var1);

    void put(final K var1, final V var2);

    void putAll(final Map<? extends K, ? extends V> var1);

    void invalidate(final Object var1);

    void invalidateAll(final Iterable<?> var1);

    void invalidateAll();

    long size();

    void cleanUp();
}
