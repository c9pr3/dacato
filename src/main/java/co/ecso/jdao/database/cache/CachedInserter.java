package co.ecso.jdao.database.cache;

import co.ecso.jdao.database.DatabaseEntity;
import co.ecso.jdao.database.internals.Inserter;
import co.ecso.jdao.database.query.DatabaseResultField;
import co.ecso.jdao.database.query.InsertQuery;

import java.util.concurrent.CompletableFuture;

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
        return Inserter.super.add(query);
    }

    <K, V> Cache<K, V> cache();
}
