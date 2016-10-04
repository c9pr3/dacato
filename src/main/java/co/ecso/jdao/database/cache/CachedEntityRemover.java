package co.ecso.jdao.database.cache;

import co.ecso.jdao.database.internals.EntityRemover;
import co.ecso.jdao.database.query.RemoveQuery;

import java.util.concurrent.CompletableFuture;

/**
 * CachedEntityRemover.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 04.10.16
 */
public interface CachedEntityRemover extends EntityRemover, CacheGetter {

    @Override
    default <S> CompletableFuture<Integer> removeOne(final RemoveQuery<S> query) {
        final CompletableFuture<Integer> rVal = EntityRemover.super.removeOne(query);
        cache().invalidateAll();
        cache().cleanUp();
        return rVal;
    }
}
