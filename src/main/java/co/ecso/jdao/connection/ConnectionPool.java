package co.ecso.jdao.connection;

import java.sql.SQLException;

/**
 * ConnectionPool.
 *
 * @param <T> Type of connection, usually "java.sql.Connection".
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 26.08.16
 */
@FunctionalInterface
public interface ConnectionPool<T> {
    /**
     * Get connection from pool.
     *
     * @return Database connection.
     * @throws SQLException if connection fails.
     * @see java.sql.Connection
     */
    T getConnection() throws SQLException;
}
