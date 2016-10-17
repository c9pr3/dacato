package co.ecso.dacato.database.cache;

import co.ecso.dacato.database.querywrapper.Query;

/**
 * CacheKeyGetter.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 21.09.16
 */
@FunctionalInterface
interface CacheKeyGetter {

    /**
     * Get cache key.
     *
     * @param <T>   Type of Query.
     * @param query Query.
     * @return Cache key.
     */
    <T> CacheKey getCacheKey(final Query<T> query);

}
