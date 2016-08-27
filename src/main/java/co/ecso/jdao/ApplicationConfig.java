package co.ecso.jdao;

import java.sql.SQLException;
import java.util.concurrent.ScheduledExecutorService;

/**
 * ApplicationConfig.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 25.08.16
 */
public interface ApplicationConfig {
    String getMysqlPoolName();

    int getMysqlMinPool();

    int getMysqlMaxPool();

    int getMysqlMaxSize();

    long getMysqlPoolIdleTimeout();

    String getMysqlHost();

    int getMysqlPort();

    String getMysqlDatabase();

    int getMysqlMaxConnections();

    String getMysqlUser();

    String getMysqlPassword();

    String getHsqlPoolName();

    int getHsqlMinPoolSize();

    int getHsqlMaxPoolSize();

    int getHsqLPoolMaxSize();

    long getHsqlPoolIdleTimeout();

    String getHsqlConnectString();

    ScheduledExecutorService getThreadPool();

    ConnectionPool getConnectionPool() throws SQLException;
}
