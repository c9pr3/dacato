package co.ecso.jdao.database;

import co.ecso.jdao.config.ApplicationConfig;
import co.ecso.jdao.database.cache.Cache;
import co.ecso.jdao.database.cache.CacheKey;
import co.ecso.jdao.database.cache.CachedEntityFinder;
import co.ecso.jdao.database.cache.CachedUpdater;
import co.ecso.jdao.database.internals.EntityFinder;
import co.ecso.jdao.database.internals.Updater;

import java.util.concurrent.CompletableFuture;

/**
 * CachedDatabaseEntity.
 *
 * @param <T> Type of this table, p.e. Long.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 17.09.16
 */
public interface CachedDatabaseEntity<T> extends DatabaseEntity<T> {

    Cache<CacheKey<?>, CompletableFuture<?>> cache();

    @Override
    default Updater<T> updater() {
        return new CachedUpdater<T>() {

            @Override
            public Cache<CacheKey<?>, CompletableFuture<?>> cache() {
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
            public Cache<CacheKey<?>, CompletableFuture<?>> cache() {
                return CachedDatabaseEntity.this.cache();
            }

            @Override
            public ApplicationConfig config() {
                return CachedDatabaseEntity.this.config();
            }
        };
    }

}
