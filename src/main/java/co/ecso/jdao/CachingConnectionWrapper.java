package co.ecso.jdao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

/**
 * CachingConnectionWrapper.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 02.07.16
 */
@SuppressWarnings("unchecked")
public final class CachingConnectionWrapper implements DatabaseConnection {
    private final DatabaseConnection databaseConnection;
    private static final Map<Integer, Cache<CacheKey, CompletableFuture<?>>> CACHE_MAP = new ConcurrentHashMap<>();
    private final ApplicationConfig config;

    public CachingConnectionWrapper(final DatabaseConnection databaseConnection, final Cache cache) {
        this.databaseConnection = databaseConnection;
        CACHE_MAP.putIfAbsent(databaseConnection.hashCode(), cache);
        this.config = databaseConnection.getConfig();
    }

    @Override
    public Connection pooledConnection() throws SQLException {
        if (this.databaseConnection == null) {
            throw new SQLException("DatabaseConnection could not be established");
        }
        return this.databaseConnection.pooledConnection();
    }

    @Override
    public ApplicationConfig getConfig() {
        return this.config;
    }

    @Override
    public CompletableFuture<?> findOne(final Query query, final CompletableFuture<?> whereId,
                                        final DatabaseField<?> column) throws SQLException {
        final CacheKey cacheKey = new CacheKey(query.getQuery(), column, whereId);
        final Cache<CacheKey, CompletableFuture<?>> cachedConnection = CACHE_MAP.get(databaseConnection.hashCode());
        try {
            return cachedConnection.get(cacheKey, () -> databaseConnection.findOne(query, cacheKey.whereId(), cacheKey.columnName()));
        } catch (final Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public CompletableFuture<Boolean> truncate(final Query query) {
        return this.databaseConnection.truncate(query).thenApply(rVal -> {
            if (rVal) {
                CACHE_MAP.get(databaseConnection.hashCode()).invalidateAll();
                CACHE_MAP.get(databaseConnection.hashCode()).cleanUp();
            }
            return rVal;
        });
    }

    @Override
    public CompletableFuture<ConcurrentLinkedQueue<Long>> findMany(final Query query) {
        final CacheKey cacheKey = new CacheKey(query.getQuery(), new DatabaseField<>("*", "", Types.VARCHAR),
                CompletableFuture.completedFuture(-1L));
        try {
            return (CompletableFuture<ConcurrentLinkedQueue<Long>>) CACHE_MAP.get(databaseConnection.hashCode()).get(cacheKey, () ->
                    this.databaseConnection.findMany(query));
        } catch (final ExecutionException e) {
            throw new RuntimeException(e);
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
//
    @Override
    public CompletableFuture<Long> insert(final Query query, final Map<DatabaseField<?>, ?> map) {
        CACHE_MAP.get(databaseConnection.hashCode()).invalidateAll();
        CACHE_MAP.get(databaseConnection.hashCode()).cleanUp();
        return this.databaseConnection.insert(query, map);
    }
//
//    @Override
//    public CompletableFuture<Boolean> update(final String tableName,
//                                             final Map<DatabaseField<?>, ? extends Comparable> map,
//                                             final CompletableFuture<?> whereId) {
//        CACHE_MAP.get(databaseConnection.hashCode()).invalidateAll();
//        CACHE_MAP.get(databaseConnection.hashCode()).cleanUp();
//        return this.databaseConnection.update(tableName, map, whereId);
//    }
//
    public static final class CacheKey implements Serializable {

        private static final long serialVersionUID = -384732894789324L;

        private final String tableName;
        private final DatabaseField<?> columnName;
        private final CompletableFuture<?> whereId;
        private final Map<DatabaseField<?>, ?> values;

        CacheKey(final String tableName, final DatabaseField<?> columnName, final CompletableFuture<?> whereId) {
            this.tableName = tableName;
            this.columnName = columnName;
            this.whereId = whereId;
            this.values = null;
        }
//
//        CacheKey(final String tableName, final Map<DatabaseField<?>, ?> values) {
//            this.tableName = tableName;
//            this.columnName = new DatabaseField<>(values.toString(), "");
//            this.whereId = null;
//            this.values = values;
//        }

//        String tableName() {
//            return tableName;
//        }

        DatabaseField<?> columnName() {
            return columnName;
        }

        CompletableFuture<?> whereId() {
            return whereId;
        }

//        public Map<DatabaseField<?>, ?> values() {
//            return values;
//        }

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
