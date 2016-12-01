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
    default CompletableFuture<DatabaseResultField<T>> add(final InsertQuery<T> query) {
        final CompletableFuture<DatabaseResultField<T>> rVal = DatabaseTable.super.add(query);
        cache().invalidateAll();
        cache().cleanUp();
        return rVal;
    }

    @Override
    default CompletableFuture<Boolean> truncate(final String query) {
        final CompletableFuture<Boolean> rVal = DatabaseTable.super.truncate(query);
        cache().invalidateAll();
        cache().cleanUp();
        return rVal;
    }

    @Override
    default <S, W> CompletableFuture<List<DatabaseResultField<S>>> findAll(final SingleColumnQuery<S, W> query) {
        final CompletableFuture<List<DatabaseResultField<S>>> rVal = findMany(query);
        cache().invalidateAll();
        cache().cleanUp();
        return rVal;
    }

    @Override
    default int statementOptions() {
        return -1;
    }

    @Override
    default Truncater truncater() {
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
        };
    }

    @Override
    default Inserter<T> inserter() {
        return new Inserter<T>() {
            @Override
            public int statementOptions() {
                return CachedDatabaseTable.this.statementOptions();
            }

            @Override
            public ApplicationConfig config() {
                return CachedDatabaseTable.this.config();
            }
        };
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
    default EntityRemover entityRemover() {
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
        };
    }

}
