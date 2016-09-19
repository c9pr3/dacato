package co.ecso.jdao.database;

import co.ecso.jdao.config.ConfigGetter;
import co.ecso.jdao.database.internals.EntityFinder;
import co.ecso.jdao.database.internals.Inserter;
import co.ecso.jdao.database.internals.Truncater;
import co.ecso.jdao.database.query.DatabaseResultField;
import co.ecso.jdao.database.query.InsertQuery;
import co.ecso.jdao.database.query.MultiColumnQuery;
import co.ecso.jdao.database.query.SingleColumnQuery;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * DatabaseTable.
 *
 * @param <T> Type of the auto_inc field of this table, usually Long.
 * @param <E> The Entity-Class which is being used.
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 03.09.16
 */
public interface DatabaseTable<T, E extends DatabaseEntity<T>> extends ConfigGetter {

    CompletableFuture<E> findOne(final T id);

    CompletableFuture<List<E>> findAll();

    default Truncater truncater() {
        return DatabaseTable.this::config;
    }

    default CompletableFuture<Boolean> truncate(final String query) {
        return this.truncater().truncate(query);
    }

    default Inserter<T, E> inserter() {
        return DatabaseTable.this::config;
    }

    default CompletableFuture<DatabaseResultField<T>> add(final InsertQuery<T> query) {
        return inserter().add(query);
    }

    default EntityFinder entityFinder() {
        return DatabaseTable.this::config;
    }

    default <S> CompletableFuture<DatabaseResultField<S>> findOne(final MultiColumnQuery<S> query) {
        return this.entityFinder().findOne(query);
    }

    default <S, W> CompletableFuture<List<DatabaseResultField<S>>> findMany(final SingleColumnQuery<S, W> query) {
        return this.entityFinder().findMany(query);
    }

    default <S, W> CompletableFuture<DatabaseResultField<S>> findOne(final SingleColumnQuery<S, W> query) {
        return this.entityFinder().findOne(query);
    }

}
