package co.ecso.jdao.database.cache;

import co.ecso.jdao.database.internals.Updater;
import co.ecso.jdao.database.query.SingleColumnUpdateQuery;

import java.util.concurrent.CompletableFuture;

/**
 * CachedUpdater.
 *
 * @param <T> Type of update -> type of query.
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 19.09.16
 */
public interface CachedUpdater<T> extends Updater<T>, CacheGetter {

    @Override
    default CompletableFuture<Boolean> update(final SingleColumnUpdateQuery<T> query) {
        cache().invalidateAll();
        cache().cleanUp();
        return Updater.super.update(query);
    }
}
