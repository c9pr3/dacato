package co.ecso.dacato.database.cache;

import co.ecso.dacato.database.query.EntityRemover;
import co.ecso.dacato.database.querywrapper.RemoveQuery;

import java.util.concurrent.CompletableFuture;

/**
 * CachedEntityRemover.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @version $Id:$
 * @since 04.10.16
 */
public interface CachedEntityRemover extends EntityRemover, CacheGetter {

    @Override
    default <S> CompletableFuture<Integer> removeOne(final RemoveQuery<S> query) {
        final CompletableFuture<Integer> rVal = EntityRemover.super.removeOne(query);
        cache().keySet().stream()
                .filter(k -> k.hasKey(query.tableName()))
                .forEach(k -> cache().invalidate(k));
        return rVal;
    }
}
