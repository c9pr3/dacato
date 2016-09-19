package co.ecso.jdao.database;

import co.ecso.jdao.config.ConfigGetter;
import co.ecso.jdao.database.internals.EntityFinder;
import co.ecso.jdao.database.internals.Updater;
import co.ecso.jdao.database.query.DatabaseResultField;
import co.ecso.jdao.database.query.SingleColumnQuery;
import co.ecso.jdao.database.query.SingleColumnUpdateQuery;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

/**
 * DatabaseEntity.
 *
 * @param <T> Type of the auto_inc field of this entity, usually Long.
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 08.08.16
 */
public interface DatabaseEntity<T> extends ConfigGetter {
    T id();

    CompletableFuture<? extends DatabaseEntity<T>> save(final ColumnList values);

    @SuppressWarnings("unused")
    String toJson() throws SQLException;

    void checkValidity();

    default CompletableFuture<Boolean> update(final SingleColumnUpdateQuery<T> query) {
        return updater().update(query);
    }

    default Updater<T> updater() {
        return DatabaseEntity.this::config;
    }

    default EntityFinder entityFinder() {
        return DatabaseEntity.this::config;
    }

    default <S, W> CompletableFuture<DatabaseResultField<S>> findOne(final SingleColumnQuery<S, W> query) {
        return this.entityFinder().findOne(query);
    }

}
