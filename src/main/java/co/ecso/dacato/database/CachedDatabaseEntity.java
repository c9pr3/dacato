package co.ecso.dacato.database;

import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.database.cache.*;
import co.ecso.dacato.database.query.EntityFinder;
import co.ecso.dacato.database.query.Updater;

import java.util.concurrent.CompletableFuture;

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
    default Updater<T> updater() {
        return new CachedUpdater<T>() {

            @Override
            public int statementOptions() {
                return CachedDatabaseEntity.this.statementOptions();
            }

            @Override
            public Cache<CacheKey, CompletableFuture> cache() {
                return CachedDatabaseEntity.this.cache();
            }

            @Override
            public ApplicationConfig config() {
                return CachedDatabaseEntity.this.config();
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
            public Cache<CacheKey, CompletableFuture> cache() {
                return CachedDatabaseEntity.this.cache();
            }

            @Override
            public ApplicationConfig config() {
                return CachedDatabaseEntity.this.config();
            }
        };
    }
}
