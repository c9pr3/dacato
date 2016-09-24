package co.ecso.jdao.database;

import co.ecso.jdao.config.ApplicationConfig;
import co.ecso.jdao.database.cache.*;
import co.ecso.jdao.database.internals.EntityFinder;
import co.ecso.jdao.database.internals.Inserter;
import co.ecso.jdao.database.internals.Truncater;
import co.ecso.jdao.database.query.DatabaseResultField;
import co.ecso.jdao.database.query.SingleColumnQuery;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * CachedDatabaseTable.
 *
 * @param <T> Type of the auto_inc field of this table, usually Long.
 * @param <E> The Entity-Class which is being used.
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 17.09.16
 */
public interface CachedDatabaseTable<T, E extends DatabaseEntity<T>> extends DatabaseTable<T, E>, CacheGetter {

    @Override
    default Truncater truncater() {
        return new CachedTruncater() {
            @Override
            public Cache<CacheKey, CompletableFuture> cache() {
                return CachedDatabaseTable.this.cache();
            }

            @Override
            public ApplicationConfig config() {
                return CachedDatabaseTable.this.config();
            }
        };
    }

    @Override
    default Inserter<T> inserter() {
        return CachedDatabaseTable.this::config;
    }

    @Override
    default CompletableFuture<Boolean> truncate(final String query) {
        cache().invalidateAll();
        cache().cleanUp();
        return DatabaseTable.super.truncate(query);
    }

    @Override
    default <S, W> CompletableFuture<List<DatabaseResultField<S>>> findAll(final SingleColumnQuery<S, W> query) {
        cache().invalidateAll();
        cache().cleanUp();
        return findMany(query);
    }

    @Override
    default EntityFinder entityFinder() {
        return new CachedEntityFinder() {
            @Override
            public Cache<CacheKey, CompletableFuture> cache() {
                return CachedDatabaseTable.this.cache();
            }

            @Override
            public ApplicationConfig config() {
                return CachedDatabaseTable.this.config();
            }
        };
    }
}
