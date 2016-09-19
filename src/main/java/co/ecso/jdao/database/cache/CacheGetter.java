package co.ecso.jdao.database.cache;

/**
 * CacheGetter.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 06.09.16
 */
@FunctionalInterface
interface CacheGetter<K, V> {
    Cache<K, V> getCache();
}
