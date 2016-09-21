package co.ecso.jdao.database;

import co.ecso.jdao.config.ConfigGetter;
import co.ecso.jdao.database.internals.EntityFinder;
import co.ecso.jdao.database.internals.Updater;
import co.ecso.jdao.database.query.DatabaseResultField;
import co.ecso.jdao.database.query.SingleColumnQuery;
import co.ecso.jdao.database.query.SingleColumnUpdateQuery;

import java.util.ConcurrentModificationException;
import java.util.concurrent.CompletableFuture;

/**
 * DatabaseEntity.
 *
 * @param <T> Type of the auto_inc (primary) field of this entity, usually Long.
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 08.08.16
 */
public interface DatabaseEntity<T> extends ConfigGetter {
    /**
     * Get primary key.
     *
     * @return Primary key.
     */
    T primaryKey();

    /**
     * Save.
     *
     * @param columnValuesToSet Column and values to save.
     * @return DatabaseEntity of type T.
     */
    CompletableFuture<? extends DatabaseEntity<T>> save(final ColumnList columnValuesToSet);

    /**
     * Check validity.
     *
     * The idea behind this is that after save, validity shall throw an exception.
     * Inside any execution, p.e. primaryKey(), the first statement has to be checkValidity.
     */
    //todo find better solution.
    void checkValidity() throws ConcurrentModificationException;

    /**
     * Wrapper for updater().update, usually called within save().
     *
     * @param query Query to execute.
     * @return True or false.
     */
    default CompletableFuture<Boolean> update(final SingleColumnUpdateQuery<T> query) {
        return updater().update(query);
    }

    /**
     * Get updater.
     *
     * @return Updater.
     */
    default Updater<T> updater() {
        return DatabaseEntity.this::config;
    }

    /**
     * Get entity finder.
     *
     * @return EntityFinder.
     */
    default EntityFinder entityFinder() {
        return DatabaseEntity.this::config;
    }

    /**
     * Find one entry.
     *
     * @param query Query to execute.
     * @param <S> Type to select, p.e. Long.
     * @param <W> Type of where, p.e. String.
     * @return DatabaseResultField of type s.
     */
    default <S, W> CompletableFuture<DatabaseResultField<S>> findOne(final SingleColumnQuery<S, W> query) {
        return this.entityFinder().findOne(query);
    }
}
