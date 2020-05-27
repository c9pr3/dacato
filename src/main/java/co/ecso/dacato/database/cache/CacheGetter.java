package co.ecso.dacato.database.cache;

/**
 * CacheGetter.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @since 21.09.16
 */
@FunctionalInterface
public interface CacheGetter {
    /**
     * Get Cache.
     *
     * @return Cache.
     */
    Cache cache();
}
