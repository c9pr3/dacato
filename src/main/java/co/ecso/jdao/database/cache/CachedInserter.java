package co.ecso.jdao.database.cache;

import co.ecso.jdao.database.DatabaseEntity;
import co.ecso.jdao.database.internals.Inserter;
import co.ecso.jdao.database.query.DatabaseResultField;
import co.ecso.jdao.database.query.InsertQuery;
import co.ecso.jdao.database.query.Query;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * CachedInserter.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 19.09.16
 */
public interface CachedInserter<T, R extends DatabaseEntity<T>> extends Inserter<T, R> {

    @Override
    default CompletableFuture<DatabaseResultField<T>> add(final InsertQuery<T> query) {
        try {
            final Cache<Query<T>, CompletableFuture<DatabaseResultField<T>>> c = cache();
            return c.get(query, () -> Inserter.super.add(query));
        } catch (final ExecutionException e) {
            final CompletableFuture<DatabaseResultField<T>> rval = new CompletableFuture<>();
            rval.completeExceptionally(e);
            return rval;
        }
    }

    <K, V> Cache<K, V> cache();
}
