package co.ecso.jdao.cache;

import co.ecso.jdao.config.ApplicationConfig;
import co.ecso.jdao.connection.ConnectionPool;
import co.ecso.jdao.database.DatabaseField;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CachingConnectionWrapper.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 02.07.16
 */
@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class CachingConnectionWrapper {
    private static final Object MUTEX = new Object();
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final Map<Integer, Cache<CacheKey<?>, CompletableFuture<?>>> CACHE_MAP = new ConcurrentHashMap<>();
    private static final Map<Integer, ConnectionPool<Connection>> CONNECTION_POOL_MAP = new ConcurrentHashMap<>();
    private final Connection databaseConnection;
    private final ApplicationConfig config;

    public CachingConnectionWrapper(final ApplicationConfig config,
                                    final Cache<CacheKey<?>, CompletableFuture<?>> cache) throws SQLException {
        synchronized (MUTEX) {
            if (!CONNECTION_POOL_MAP.containsKey(config.hashCode())) {
                CONNECTION_POOL_MAP.putIfAbsent(config.hashCode(), config.getConnectionPool());
            }
            this.databaseConnection = CONNECTION_POOL_MAP.get(config.hashCode()).getConnection();
            this.config = config;
            CACHE_MAP.putIfAbsent(databaseConnection.hashCode(), cache);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static final class CacheKey<T> implements Serializable {

        private static final long serialVersionUID = -384732894789324L;

        private final String tableName;
        private final DatabaseField<?> columnName;
        private final transient CompletableFuture<T> whereId;
        private final transient Map<DatabaseField<?>, ?> values;

        CacheKey(final String tableName, final DatabaseField<?> columnName, final CompletableFuture<T> whereId) {
            this.tableName = tableName;
            this.columnName = columnName;
            this.whereId = whereId;
            this.values = null;
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
