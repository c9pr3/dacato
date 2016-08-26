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
public interface ConnectionPool {

//    default ConnectionPool build(final String mysqlPoolName, final int mysqlMinPool, final int mysqlMaxPool,
//                          final int mysqlMaxSize, final long mysqlPoolIdleTimeout, final String mysqlConnectionString,
//                          final String mysqlUser, final String mysqlPassword) {
//        return new ConnectionPool() {
//            @Override
//            public Connection getConnection() {
//                return null;
//            }
//        };
//    }
//
    Connection getConnection() throws SQLException;
}
