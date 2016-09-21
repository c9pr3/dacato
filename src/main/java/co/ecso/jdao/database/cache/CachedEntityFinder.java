package co.ecso.jdao.database.cache;

import co.ecso.jdao.database.internals.EntityFinder;
import co.ecso.jdao.database.query.DatabaseResultField;
import co.ecso.jdao.database.query.MultiColumnQuery;
import co.ecso.jdao.database.query.SingleColumnQuery;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * CachedEntityFinder.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 19.09.16
 */
public interface CachedEntityFinder extends EntityFinder, CacheGetter {

    @Override
    default <S, W> CompletableFuture<List<DatabaseResultField<S>>> findMany(final SingleColumnQuery<S, W> query) {
        try {
            //@TODO find better solution than unchecked cast
            //noinspection unchecked
            return (CompletableFuture<List<DatabaseResultField<S>>>) cache().get(query.getCacheKey(), () ->
                    EntityFinder.super.findMany(query));
        } catch (final ExecutionException e) {
            final CompletableFuture<List<DatabaseResultField<S>>> rval = new CompletableFuture<>();
            rval.completeExceptionally(e);
            return rval;
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    default <S> CompletableFuture<DatabaseResultField<S>> findOne(final MultiColumnQuery<S> query) {
        try {
            //@TODO find better solution than unchecked cast
            //noinspection unchecked
            return (CompletableFuture<DatabaseResultField<S>>) cache().get(query.getCacheKey(), () ->
                    EntityFinder.super.findOne(query));
        } catch (final ExecutionException e) {
            final CompletableFuture<DatabaseResultField<S>> rval = new CompletableFuture<>();
            rval.completeExceptionally(e);
            return rval;
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    default <S, W> CompletableFuture<DatabaseResultField<S>> findOne(final SingleColumnQuery<S, W> query) {
        try {
            //@TODO find better solution than unchecked cast
            //noinspection unchecked
            return (CompletableFuture<DatabaseResultField<S>>) cache().get(query.getCacheKey(), () ->
                    EntityFinder.super.findOne(query));
        } catch (final ExecutionException e) {
            final CompletableFuture<DatabaseResultField<S>> rval = new CompletableFuture<>();
            rval.completeExceptionally(e);
            return rval;
        }
    }

}
