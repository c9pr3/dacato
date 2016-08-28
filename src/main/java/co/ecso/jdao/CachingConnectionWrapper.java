package co.ecso.jdao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
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
public class CachingConnectionWrapper implements DatabaseConnection {
    private static final Map<Integer, Cache<CacheKey, CompletableFuture<?>>> CACHE_MAP = new ConcurrentHashMap<>();
    private final DatabaseConnection databaseConnection;
    private final ApplicationConfig config;

    public CachingConnectionWrapper(final DatabaseConnection databaseConnection, final ApplicationConfig config,
                                    final Cache cache) {
        this.databaseConnection = databaseConnection;
        synchronized (CACHE_MAP) {
            CACHE_MAP.putIfAbsent(databaseConnection.hashCode(), cache);
        }
        this.config = config;
    }

    @Override
    public Connection pooledConnection() throws SQLException {
        if (this.databaseConnection == null) {
            throw new SQLException("DatabaseConnection could not be established");
        }
        return this.databaseConnection.pooledConnection();
    }

    public CompletableFuture<?> findOne(final Query query, final DatabaseField<?> column,
                                        final CompletableFuture<Long> whereIdFuture) throws ExecutionException {
        synchronized (CACHE_MAP) {
            final CacheKey cacheKey = new CacheKey<>(query.getQuery(), column, whereIdFuture);
            return CACHE_MAP.get(databaseConnection.hashCode()).get(cacheKey, () ->
                    ((Finder<Long>) () -> config)
                            .findOne(query, cacheKey.columnName(), cacheKey.whereId()));
        }
    }


    public CompletableFuture<?> findOne(final Query query, final DatabaseField<?> column,
                                        final Map<DatabaseField<?>, ?> columnsToSelect) throws ExecutionException {
        synchronized (CACHE_MAP) {
            final CacheKey cacheKey = new CacheKey<>(query.getQuery(), column,
                    CompletableFuture.completedFuture(columnsToSelect));
            return CACHE_MAP.get(databaseConnection.hashCode()).get(cacheKey, () ->
                    ((Finder<Long>) () -> config)
                            .findOne(query, cacheKey.columnName(), columnsToSelect));
        }
    }

    public CompletableFuture<Boolean> truncate(final Query query) {
        synchronized (CACHE_MAP) {
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

    public CompletableFuture<LinkedList<?>> findMany(final Query query, DatabaseField<?> selector,
                                                        Map<DatabaseField<?>, ?> map) throws SQLException,
            ExecutionException {
        synchronized (CACHE_MAP) {
            final CacheKey cacheKey = new CacheKey(query.getQuery(), selector, CompletableFuture.completedFuture(null));
            return (CompletableFuture<LinkedList<?>>) CACHE_MAP.get(databaseConnection.hashCode())
                    .get(cacheKey, () -> ((Finder<Long>) () -> config).findMany(query, selector, map));
        }
    }

//    @Override
//    public CompletableFuture<Long> selectIdWithValues(final Query query, final Map<DatabaseField<?>, ?> values) {
//        final CacheKey cacheKey = new CacheKey(tableName, values);
//        try {
//            return (CompletableFuture<Long>) CACHE_MAP.get(databaseConnection.hashCode()).get(cacheKey, () ->
//                    this.databaseConnection.selectIdWithValues(cacheKey.tableName(), cacheKey.values()));
//        } catch (final ExecutionException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public CompletableFuture<List<Long>> selectIdsWithValues(final String tableName,
//                                                             final Map<DatabaseField<?>, ?> values) {
//        final CacheKey cacheKey = new CacheKey(tableName, values);
//        try {
//            return (CompletableFuture<List<Long>>) CACHE_MAP.get(databaseConnection.hashCode()).get(cacheKey, () ->
//                    this.databaseConnection.selectIdsWithValues(tableName, values));
//        } catch (final ExecutionException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public CompletableFuture<Long> insert(final Query query, final Map<DatabaseField<?>, ?> map) {
        synchronized (CACHE_MAP) {
            CACHE_MAP.get(databaseConnection.hashCode()).invalidateAll();
            CACHE_MAP.get(databaseConnection.hashCode()).cleanUp();
        }
        return ((Inserter<Long>) () -> config).insert(query, map);
    }

    //    @Override
//    public CompletableFuture<Boolean> update(final String tableName,
//                                             final Map<DatabaseField<?>, ? extends Comparable> map,
//                                             final CompletableFuture<?> whereId) {
//        CACHE_MAP.get(databaseConnection.hashCode()).invalidateAll();
//        CACHE_MAP.get(databaseConnection.hashCode()).cleanUp();
//        return this.databaseConnection.update(tableName, map, whereId);
//    }
//
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
