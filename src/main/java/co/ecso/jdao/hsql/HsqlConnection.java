package co.ecso.jdao.hsql;

import co.ecso.jdao.ApplicationConfig;
import co.ecso.jdao.ConnectionPool;
import co.ecso.jdao.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * HsqlConnection.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 25.04.16
 */
@SuppressWarnings("unused")
public final class HsqlConnection implements DatabaseConnection {
    private static final Map<Integer, ConnectionPool<Connection>> CONNECTION_POOL_MAP = new ConcurrentHashMap<>();
    private final ApplicationConfig config;

    public HsqlConnection(final ApplicationConfig config) {
        this.config = config;
        if (!CONNECTION_POOL_MAP.containsKey(config.hashCode())) {
            CONNECTION_POOL_MAP.putIfAbsent(config.hashCode(), config.getConnectionPool());
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    public Connection pooledConnection() throws SQLException {
        final ConnectionPool<Connection> pool = CONNECTION_POOL_MAP.get(config.hashCode());
        final Connection connection = pool.getConnection();
        if (connection == null) {
            throw new SQLException(String.format("Could not get connection from pool %s",
                    getConfig().getHsqlPoolName()));
        }
        return connection;
    }

    @Override
    public ApplicationConfig getConfig() {
        return this.config;
    }

}
