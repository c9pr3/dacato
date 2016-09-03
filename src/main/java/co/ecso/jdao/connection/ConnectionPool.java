package co.ecso.jdao.connection;

import java.sql.SQLException;

/**
 * ConnectionPool.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 26.08.16
 */
@SuppressWarnings("WeakerAccess")
@FunctionalInterface
public interface ConnectionPool<T> {
    T getConnection() throws SQLException;
}
