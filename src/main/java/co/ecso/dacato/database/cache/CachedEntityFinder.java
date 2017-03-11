package co.ecso.dacato.database.cache;

import co.ecso.dacato.database.query.EntityFinder;
import co.ecso.dacato.database.querywrapper.DatabaseResultField;
import co.ecso.dacato.database.querywrapper.MultiColumnQuery;
import co.ecso.dacato.database.querywrapper.Query;
import co.ecso.dacato.database.querywrapper.SingleColumnQuery;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * CachedEntityFinder.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 19.09.16
 */
public interface CachedEntityFinder extends EntityFinder, CacheGetter, CacheKeyGetter {

    @Override
    default <T> CacheKey getCacheKey(final Query<T> query) {
        return new CacheKey(query.queryType(), query.query(), query.toString());
    }

    @Override
    default <S, W> CompletableFuture<List<DatabaseResultField<S>>> findMany(final SingleColumnQuery<S, W> query,
                                                                            final Callable<AtomicBoolean>
                                                                                    validityCallback) {
        try {
            final Cache cache = cache();
            return cache.get(getCacheKey(query), () -> EntityFinder.super.findMany(query, validityCallback));
        } catch (final ExecutionException e) {
            e.printStackTrace();
            final CompletableFuture<List<DatabaseResultField<S>>> rval = new CompletableFuture<>();
            rval.completeExceptionally(e);
            return rval;
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    default <S> CompletableFuture<DatabaseResultField<S>> findOne(final MultiColumnQuery<S> query,
                                                                  final Callable<AtomicBoolean> validityCallback) {
        try {
            final Cache cache = cache();
            return cache.get(getCacheKey(query), () -> EntityFinder.super.findOne(query, validityCallback));
        } catch (final ExecutionException e) {
            final CompletableFuture<DatabaseResultField<S>> rval = new CompletableFuture<>();
            rval.completeExceptionally(e);
            return rval;
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    default <S, W> CompletableFuture<DatabaseResultField<S>> findOne(final SingleColumnQuery<S, W> query,
                                                                     final Callable<AtomicBoolean> validityCallback) {
        try {
            final Cache cache = cache();
            return cache.get(getCacheKey(query), () -> EntityFinder.super.findOne(query, validityCallback));
        } catch (final ExecutionException e) {
            final CompletableFuture<DatabaseResultField<S>> rval = new CompletableFuture<>();
            rval.completeExceptionally(e);
            return rval;
        }
    }

}
