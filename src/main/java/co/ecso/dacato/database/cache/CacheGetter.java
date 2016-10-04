package co.ecso.dacato.database.cache;

import java.util.concurrent.CompletableFuture;

/**
 * CacheGetter.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 21.09.16
 */
@FunctionalInterface
public interface CacheGetter {
    /**
     * Get Cache.
     *
     * @return Cache.
     */
    Cache<CacheKey, CompletableFuture> cache();
}
