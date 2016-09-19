package co.ecso.jdao.database.cache;

import co.ecso.jdao.database.internals.Truncater;

import java.util.concurrent.CompletableFuture;

/**
 * CachedTruncater.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 19.09.16
 */
public interface CachedTruncater extends Truncater {

    @Override
    default CompletableFuture<Boolean> truncate(final String query) {
        cache().invalidateAll();
        cache().cleanUp();
        return Truncater.super.truncate(query);
    }

    <K, V> Cache<K, V> cache();
}
