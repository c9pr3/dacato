package co.ecso.dacato.database;

import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.database.cache.Cache;
import co.ecso.dacato.database.cache.CacheGetter;
import co.ecso.dacato.database.cache.CachedEntityFinder;
import co.ecso.dacato.database.cache.CachedUpdater;
import co.ecso.dacato.database.query.EntityFinder;
import co.ecso.dacato.database.query.Updater;
import co.ecso.dacato.database.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * CachedDatabaseEntity.
 *
 * @param <T> Type of this table, p.e. Long.
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 17.09.16
 */
public interface CachedDatabaseEntity<T> extends DatabaseEntity<T>, CacheGetter {

    @Override
    default int statementOptions() {
        return -1;
    }

    @Override
    default Updater<T> updater(final Transaction transaction) {
        return new CachedUpdater<T>() {

            @Override
            public int statementOptions() {
                return CachedDatabaseEntity.this.statementOptions();
            }

            @Override
            public Cache cache() {
                return CachedDatabaseEntity.this.cache();
            }

            @Override
            public ApplicationConfig config() {
                return CachedDatabaseEntity.this.config();
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
    default EntityFinder entityFinder() {
        return new CachedEntityFinder() {

            @Override
            public int statementOptions() {
                return CachedDatabaseEntity.this.statementOptions();
            }

            @Override
            public Cache cache() {
                return CachedDatabaseEntity.this.cache();
            }

            @Override
            public ApplicationConfig config() {
                return CachedDatabaseEntity.this.config();
            }
        };
    }
}
