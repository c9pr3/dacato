package co.ecso.jdao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PooledDatabaseConnection.
 * Decorator for static JDBCConnector.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 16.03.16
 */
class PooledDatabaseConnection {
    private static final Map<Integer, ConnectionPool<Connection>> CONNECTION_POOL_MAP = new ConcurrentHashMap<>();
    private ApplicationConfig config;

    PooledDatabaseConnection(final ApplicationConfig config) throws SQLException {
        this.config = config;
        if (!CONNECTION_POOL_MAP.containsKey(config.hashCode())) {
            CONNECTION_POOL_MAP.putIfAbsent(config.hashCode(), config.getConnectionPool());
        }
    }

    Connection pooledConnection() throws SQLException {
        final Connection connection = CONNECTION_POOL_MAP.get(config.hashCode()).getConnection();
        if (connection == null) {
            throw new SQLException("Could not get connection from pool");
        }
        return connection;
    }

}
