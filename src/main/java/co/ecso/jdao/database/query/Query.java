package co.ecso.jdao.database.query;

import co.ecso.jdao.database.cache.CacheKeyGetter;

/**
 * Query.
 *
 * @param <T> Needed for CacheKeyGetter only.
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 19.09.16
 */
//@TODO CacheKeyGetter is usually not needed for queries. Only if cached. Find a better solution.
interface Query<T> extends CacheKeyGetter<T> {

    /**
     * Get query.
     *
     * @return Query.
     */
    String query();

}
