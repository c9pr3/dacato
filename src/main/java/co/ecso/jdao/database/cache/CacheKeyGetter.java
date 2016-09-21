package co.ecso.jdao.database.cache;

/**
 * CacheKeyGetter.
 *
 * @param <T> Type of cache key.
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 21.09.16
 */
@FunctionalInterface
public interface CacheKeyGetter<T> {

    /**
     * Get cache key.
     *
     * @return Cache key.
     */
    CacheKey<T> getCacheKey();

}
