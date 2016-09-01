package co.ecso.jdao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * CachingConnectionWrapper.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 02.07.16
 */
@SuppressWarnings({"unchecked", "WeakerAccess"})
public class CachingConnectionWrapper {
    private static final Object MUTEX = new Object();
    private static final Map<Integer, Cache<CacheKey, CompletableFuture<?>>> CACHE_MAP = new ConcurrentHashMap<>();
    private static final Map<Integer, ConnectionPool<Connection>> CONNECTION_POOL_MAP = new ConcurrentHashMap<>();
    private final Connection databaseConnection;
    private final ApplicationConfig config;

    public CachingConnectionWrapper(final ApplicationConfig config, final Cache cache) throws SQLException {
        synchronized (MUTEX) {
            if (!CONNECTION_POOL_MAP.containsKey(config.hashCode())) {
                CONNECTION_POOL_MAP.putIfAbsent(config.hashCode(), config.getConnectionPool());
            }
            this.databaseConnection = CONNECTION_POOL_MAP.get(config.hashCode()).getConnection();
            this.config = config;
            CACHE_MAP.putIfAbsent(databaseConnection.hashCode(), cache);
        }
    }

    public final CompletableFuture<?> findOne(final Query query, final DatabaseField<?> column,
                                        final CompletableFuture<Long> whereIdFuture) throws ExecutionException {
        synchronized (MUTEX) {
            final CacheKey cacheKey = new CacheKey<>(query.getQuery(), column, whereIdFuture);
            return CACHE_MAP.get(databaseConnection.hashCode()).get(cacheKey, () ->
                    ((Finder<Long>) () -> config)
                            .findOne(query, cacheKey.columnName(), cacheKey.whereId()));
        }
    }

    public final CompletableFuture<?> findOne(final Query query, final DatabaseField<?> column,
                                        final LinkedHashMap<DatabaseField<?>, ?> columnsToSelect)
            throws ExecutionException {
        synchronized (MUTEX) {
            final CacheKey cacheKey = new CacheKey<>(query.getQuery(), column,
                    CompletableFuture.completedFuture(columnsToSelect));
            return CACHE_MAP.get(databaseConnection.hashCode()).get(cacheKey, () ->
                    ((Finder<Long>) () -> config)
                            .findOne(query, cacheKey.columnName(), columnsToSelect));
        }
    }

    public final CompletableFuture<Boolean> truncate(final Query query) {
        synchronized (MUTEX) {
            return ((Truncater) () -> config).truncate(query)
                    .thenApply(rVal -> {
                        if (rVal) {
                            CACHE_MAP.get(databaseConnection.hashCode()).invalidateAll();
                            CACHE_MAP.get(databaseConnection.hashCode()).cleanUp();
                        }
                        return rVal;
                    });
        }
    }

    public final CompletableFuture<List<?>> findMany(final Query query, final DatabaseField<?> selector,
                                               final LinkedHashMap<DatabaseField<?>, ?> map) throws SQLException,
            ExecutionException {
        synchronized (MUTEX) {
            final CacheKey cacheKey = new CacheKey(query.getQuery(), selector, CompletableFuture.completedFuture(null));
            return (CompletableFuture<List<?>>) CACHE_MAP.get(databaseConnection.hashCode())
                    .get(cacheKey, () -> ((Finder<Long>) () -> config).findMany(query, selector, map));
        }
    }

    public final CompletableFuture<Long> insert(final Query query, final LinkedHashMap<DatabaseField<?>, ?> map) {
        synchronized (MUTEX) {
            CACHE_MAP.get(databaseConnection.hashCode()).invalidateAll();
            CACHE_MAP.get(databaseConnection.hashCode()).cleanUp();
        }
        return ((Inserter<Long>) () -> config).insert(query, map);
    }

    public CompletableFuture<Boolean> update(final Query query, final LinkedHashMap<DatabaseField<?>, ?> map,
                                             final CompletableFuture<?> whereId) {
        CACHE_MAP.get(databaseConnection.hashCode()).invalidateAll();
        CACHE_MAP.get(databaseConnection.hashCode()).cleanUp();
        return ((Updater) () -> config).update(query, map, whereId);
    }

    @SuppressWarnings("WeakerAccess")
    public static final class CacheKey<T> implements Serializable {

        private static final long serialVersionUID = -384732894789324L;

        private final String tableName;
        private final DatabaseField<?> columnName;
        private final CompletableFuture<T> whereId;
        private final Map<DatabaseField<?>, ?> values;

        CacheKey(final String tableName, final DatabaseField<?> columnName, final CompletableFuture<T> whereId) {
            this.tableName = tableName;
            this.columnName = columnName;
            this.whereId = whereId;
            this.values = null;
        }

        DatabaseField<?> columnName() {
            return columnName;
        }

        CompletableFuture<T> whereId() {
            return whereId;
        }

        @Override
        public String toString() {
            return "CacheKey{" +
                    "tableName='" + tableName + '\'' +
                    ", columnName='" + columnName + '\'' +
                    ", whereId=" + whereId +
                    ", values=" + values +
                    '}';
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final CacheKey cacheKey = (CacheKey) o;
            return whereId == cacheKey.whereId &&
                    Objects.equals(tableName, cacheKey.tableName) &&
                    Objects.equals(columnName, cacheKey.columnName) &&
                    Objects.equals(values, cacheKey.values);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tableName, columnName, whereId, values);
        }
    }

}
