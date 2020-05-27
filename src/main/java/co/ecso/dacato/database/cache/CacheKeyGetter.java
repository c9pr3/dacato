package co.ecso.dacato.database.cache;

import co.ecso.dacato.database.querywrapper.Query;

/**
 * CacheKeyGetter.
 *
 * @author Christian Scharmach (cs@e-cs.co)
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
    <T> CacheKey<Object> getCacheKey(final Query<T> query);

}
