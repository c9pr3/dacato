package co.ecso.jdao.database.query;

import co.ecso.jdao.database.cache.CacheKey;

/**
 * Query.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 19.09.16
 */
public interface Query<T> {

    String query();

    CacheKey<T> getCacheKey();

}
