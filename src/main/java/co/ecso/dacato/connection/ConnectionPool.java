package co.ecso.dacato.connection;

import java.sql.SQLException;

/**
 * ConnectionPool.
 *
 * @param <T> Type of connection, usually "java.sql.Connection".
 * @author Christian Scharmach (cs@e-cs.co)
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
