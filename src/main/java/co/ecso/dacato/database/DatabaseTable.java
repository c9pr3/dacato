package co.ecso.dacato.database;

import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.config.ConfigGetter;
import co.ecso.dacato.database.query.EntityFinder;
import co.ecso.dacato.database.query.EntityRemover;
import co.ecso.dacato.database.query.Inserter;
import co.ecso.dacato.database.query.Truncater;
import co.ecso.dacato.database.querywrapper.*;
import co.ecso.dacato.database.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

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

    /**
     * Default statement options.
     *
     * @return Options for all statements.
     */
    default int statementOptions() {
        return -1;
    }

    /**
     * Get EntityFinder.
     *
     * @return Entity finder
     */
    default EntityFinder entityFinder() {
        return new EntityFinder() {
            @Override
            public int statementOptions() {
                return DatabaseTable.this.statementOptions();
            }

            @Override
            public ApplicationConfig config() {
                return DatabaseTable.this.config();
            }
        };
    }

    /**
     * Get EntityRemover.
     *
     * @return Entity Remover.
     */
    @SuppressWarnings("Duplicates")
    default EntityRemover entityRemover(final Transaction transaction) {
        return new EntityRemover() {
            @Override
            public int statementOptions() {
                return DatabaseTable.this.statementOptions();
            }

            @Override
            public ApplicationConfig config() {
                return DatabaseTable.this.config();
            }

            @Override
            public Transaction transaction() {
                return transaction;
            }

            @Override
            public Connection connection() throws SQLException {
                if (transaction != null) {
                    return transaction.connection();
                } else {
                    return config().databaseConnectionPool().getConnection();
                }
            }
        };
    }

    /**
     * Remove one entry.
     *
     * @param query Remove Query.
     * @return Boolean.
     */
    default <S> CompletableFuture<Integer> removeOne(final RemoveQuery<S> query) {
        return this.entityRemover(null).removeOne(query);
    }

    /**
     * Remove one entry.
     *
     * @param query Remove Query.
     * @return Boolean.
     */
    default <S> CompletableFuture<Integer> removeOne(final RemoveQuery<S> query, Transaction transaction) {
        return this.entityRemover(transaction).removeOne(query);
    }

    /**
     * Find one by primary key.
     *
     * @param primaryKey Primary key.
     * @return DatabasEntity (E) of type T.
     */
    CompletableFuture<E> findOne(final T primaryKey);

    /**
     * Find one with single column query.
     *
     * @param query Query.
     * @param <S>   Type of select, therefore type of return.
     * @param <W>   Type of where.
     * @return DatabaseResultField of type S.
     */
    default <S, W> CompletableFuture<DatabaseResultField<S>> findOne(final SingleColumnQuery<S, W> query) {
        return this.entityFinder().findOne(query, this::alwaysValid);
    }

    default AtomicBoolean alwaysValid() {
        return new AtomicBoolean(true);
    }

    /**
     * Find one with multi column query.
     *
     * @param query Query.
     * @param <S>   Type of query and therefore result.
     * @return DatabaseResultField of type S.
     */
    default <S> CompletableFuture<DatabaseResultField<S>> findOne(final MultiColumnQuery<S> query) {
        return this.entityFinder().findOne(query, this::alwaysValid);
    }

    /**
     * Find one with multi column query.
     *
     * @param query Query.
     * @return DatabaseResultField of type S.
     */
    default CompletableFuture<Map<DatabaseField, DatabaseResultField>> findOne(
            final MultiColumnSelectQuery<?> query) {
        return this.entityFinder().findOne(query, this::alwaysValid);
    }

    /**
     * Find many.
     *
     * @param query Query.
     * @param <S>   Type of select, therefore type of return.
     * @param <W>   Type of where.
     * @return List of DatabaseResultFields of type S.
     */
    default <S, W> CompletableFuture<List<DatabaseResultField<S>>> findMany(final SingleColumnQuery<S, W> query) {
        return this.entityFinder().findMany(query, this::alwaysValid);
    }

    /**
     * Find many.
     *
     * @param query Query.
     * @return List of DatabaseResultFields of type S.
     */
    default CompletableFuture<List<Map<DatabaseField, DatabaseResultField>>> findMany(
            final MultiColumnSelectQuery<?> query) {
        return this.entityFinder().findMany(query, this::alwaysValid);
    }

    /**
     * Find all entries.
     *
     * @return List of DatabasEntity (E) of type T.
     */
    CompletableFuture<List<E>> findAll();

    /**
     * Find all, usually called within findAll().
     *
     * @param query Query to execute.
     * @param <S>   Type to select.
     * @param <W>   Type of where.
     * @return List of DatabaseResultFields of type S.
     */
    default <S, W> CompletableFuture<List<DatabaseResultField<S>>> findAll(final SingleColumnQuery<S, W> query) {
        return this.findMany(query);
    }

    /**
     * Get truncater.
     *
     * @return Truncater.
     */
    @SuppressWarnings("Duplicates")
    default Truncater truncater(final Transaction transaction) {
        return new Truncater() {
            @Override
            public int statementOptions() {
                return DatabaseTable.this.statementOptions();
            }

            @Override
            public ApplicationConfig config() {
                return DatabaseTable.this.config();
            }

            @Override
            public Transaction transaction() {
                return transaction;
            }

            @Override
            public Connection connection() throws SQLException {
                if (transaction != null) {
                    return transaction.connection();
                } else {
                    return config().databaseConnectionPool().getConnection();
                }
            }
        };
    }

    /**
     * Truncate a table.
     *
     * @param query Query to execute.
     * @return True or false.
     */
    default CompletableFuture<Boolean> truncate(final String query) {
        return this.truncater(null).truncate(query);
    }

    /**
     * Truncate a table.
     *
     * @param query Query to execute.
     * @return True or false.
     */
    default CompletableFuture<Boolean> truncate(final String query, final Transaction transaction) {
        return this.truncater(transaction).truncate(query);
    }

    /**
     * Get Inserter.
     *
     * @return Inserter.
     */
    default Inserter<T> inserter(final Transaction transaction) {
        return new Inserter<T>() {
            @Override
            public Connection connection() throws SQLException {
                if (transaction != null) {
                    return transaction.connection();
                } else {
                    return config().databaseConnectionPool().getConnection();
                }
            }

            @Override
            public int statementOptions() {
                return DatabaseTable.this.statementOptions();
            }

            @Override
            public ApplicationConfig config() {
                return DatabaseTable.this.config();
            }

            @Override
            public Transaction transaction() {
                return transaction;
            }
        };
    }

    /**
     * Add a row.
     *
     * @param query Query to execute.
     * @return DatbaseResultField of type T.
     */
    default CompletableFuture<DatabaseResultField<T>> add(final InsertQuery<T> query) {
        return inserter(null).add(query);
    }

    /**
     * Add a row with transaction.
     *
     * @param query Query to execute.
     * @param transaction Transaction.
     * @return DatbaseResultField of type T.
     */
    default CompletableFuture<DatabaseResultField<T>> add(final InsertQuery<T> query, final Transaction transaction) {
        return inserter(transaction).add(query);
    }

}
