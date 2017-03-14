package co.ecso.dacato.database;

import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.database.cache.*;
import co.ecso.dacato.database.query.EntityFinder;
import co.ecso.dacato.database.query.EntityRemover;
import co.ecso.dacato.database.query.Inserter;
import co.ecso.dacato.database.query.Truncater;
import co.ecso.dacato.database.querywrapper.DatabaseResultField;
import co.ecso.dacato.database.querywrapper.InsertQuery;
import co.ecso.dacato.database.querywrapper.SingleColumnQuery;
import co.ecso.dacato.database.querywrapper.TruncateQuery;
import co.ecso.dacato.database.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * CachedDatabaseTable.
 *
 * @param <T> Type of the auto_inc field of this table, usually Long.
 * @param <E> The Entity-Class which is being used.
 * @author Christian Senkowski (cs@2scale.net)
 * @since 17.09.16
 */
public interface CachedDatabaseTable<T, E extends DatabaseEntity<T>> extends DatabaseTable<T, E>, CacheGetter {

    @Override
    default CompletableFuture<DatabaseResultField<T>> add(final InsertQuery<T> query, final Transaction transaction) {
        final CompletableFuture<DatabaseResultField<T>> rVal = DatabaseTable.super.add(query, transaction);
        cache().keySet().stream()
                .filter(ck -> ck.hasKey(query.tableName()))
                .forEach(ck -> cache().invalidate(ck));
        return rVal;
    }

    @Override
    default CompletableFuture<Boolean> truncate(final TruncateQuery<?> query, final Transaction transaction) {
        final CompletableFuture<Boolean> rVal = DatabaseTable.super.truncate(query, transaction);
        cache().keySet().stream()
                .filter(ck -> ck.hasKey(query.tableName()))
                .forEach(ck -> cache().invalidate(ck));
        return rVal;
    }

    @Override
    default <S, W> CompletableFuture<List<DatabaseResultField<S>>> findAll(final String tableName, final SingleColumnQuery<S, W> query) {
        final CompletableFuture<List<DatabaseResultField<S>>> rVal = findMany(query);
        cache().keySet().stream()
                .filter(ck -> ck.hasKey(tableName))
                .forEach(ck -> cache().invalidate(ck));
        return rVal;
    }

    @Override
    default EntityFinder entityFinder() {
        return new CachedEntityFinder() {
            @Override
            public int statementOptions() {
                return CachedDatabaseTable.this.statementOptions();
            }

            @Override
            public Cache cache() {
                return CachedDatabaseTable.this.cache();
            }

            @Override
            public ApplicationConfig config() {
                return CachedDatabaseTable.this.config();
            }
        };
    }

    @Override
    default Truncater truncater(final Transaction transaction) {
        return new CachedTruncater() {
            @Override
            public int statementOptions() {
                return CachedDatabaseTable.this.statementOptions();
            }

            @Override
            public Cache cache() {
                return CachedDatabaseTable.this.cache();
            }

            @Override
            public ApplicationConfig config() {
                return CachedDatabaseTable.this.config();
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

    @Override
    default Inserter<T> inserter(final Transaction transaction) {
        return new Inserter<T>() {
            @Override
            public int statementOptions() {
                return CachedDatabaseTable.this.statementOptions();
            }

            @Override
            public ApplicationConfig config() {
                return CachedDatabaseTable.this.config();
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

    @Override
    default EntityRemover entityRemover(final Transaction transaction) {
        return new CachedEntityRemover() {
            @Override
            public int statementOptions() {
                return CachedDatabaseTable.this.statementOptions();
            }

            @Override
            public ApplicationConfig config() {
                return CachedDatabaseTable.this.config();
            }

            @Override
            public Cache cache() {
                return CachedDatabaseTable.this.cache();
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

}
