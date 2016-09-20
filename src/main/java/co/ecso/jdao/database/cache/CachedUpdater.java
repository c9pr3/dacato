package co.ecso.jdao.database.cache;

import co.ecso.jdao.database.internals.Updater;
import co.ecso.jdao.database.query.DatabaseResultField;
import co.ecso.jdao.database.query.SingleColumnUpdateQuery;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * CachedUpdater.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 19.09.16
 */
public interface CachedUpdater<T> extends Updater<T> {

    @Override
    default CompletableFuture<Boolean> update(final SingleColumnUpdateQuery<T> query) {
        cache().invalidateAll();
        cache().cleanUp();
        return Updater.super.update(query);
    }

    Cache<CacheKey<?>, CompletableFuture<?>> cache();

}
