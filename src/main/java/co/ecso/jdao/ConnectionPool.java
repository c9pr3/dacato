package co.ecso.jdao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * ConnectionPool.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 26.08.16
 */
@FunctionalInterface
public interface ConnectionPool {
    Connection getConnection() throws SQLException;
}
