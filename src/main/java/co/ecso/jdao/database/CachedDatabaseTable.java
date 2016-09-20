package co.ecso.jdao.database;

import co.ecso.jdao.config.ApplicationConfig;
import co.ecso.jdao.database.cache.Cache;
import co.ecso.jdao.database.cache.CachedEntityFinder;
import co.ecso.jdao.database.cache.CachedTruncater;
import co.ecso.jdao.database.internals.EntityFinder;
import co.ecso.jdao.database.internals.Inserter;
import co.ecso.jdao.database.internals.Truncater;

/**
 * CachedDatabaseTable.
 *
 * @param <T> Type of the auto_inc field of this table, usually Long.
 * @param <E> The Entity-Class which is being used.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 17.09.16
 */
public interface CachedDatabaseTable<T, E extends DatabaseEntity<T>> extends DatabaseTable<T, E> {

    @Override
    default Truncater truncater() {
        return new CachedTruncater() {
            @Override
            public <K, V> Cache<K, V> cache() {
                return CachedDatabaseTable.this.cache();
            }

            @Override
            public ApplicationConfig config() {
                return CachedDatabaseTable.this.config();
            }
        };
    }

    @Override
    default Inserter<T, E> inserter() {
        return CachedDatabaseTable.this::config;
    }

    @Override
    default EntityFinder entityFinder() {
        return new CachedEntityFinder() {
            @Override
            public <K, V> Cache<K, V> cache() {
                return CachedDatabaseTable.this.cache();
            }

            @Override
            public ApplicationConfig config() {
                return CachedDatabaseTable.this.config();
            }
        };
    }

    <K, V> Cache<K,V> cache();

}
