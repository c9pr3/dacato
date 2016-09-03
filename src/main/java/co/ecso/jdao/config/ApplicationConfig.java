package co.ecso.jdao.config;

import co.ecso.jdao.connection.ConnectionPool;

import java.sql.Connection;
import java.util.concurrent.ScheduledExecutorService;

/**
 * ApplicationConfig.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 25.08.16
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public interface ApplicationConfig {

    String getMysqlHost();

    int getMysqlPort();

    int getMaxConnections();

    String getPoolName();

    int getMinPoolSize();

    int getMaxPoolSize();

    int getPoolMaxSize();

    long getPoolIdleTimeout();

    String getConnectString();

    ScheduledExecutorService getThreadPool();

    ConnectionPool<Connection> getConnectionPool();
}
