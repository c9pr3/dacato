package co.ecso.jdao.database.cache;

/**
 * CacheKey.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 20.09.16
 */
@FunctionalInterface
public interface CacheKey<T> {
    String create();
}
