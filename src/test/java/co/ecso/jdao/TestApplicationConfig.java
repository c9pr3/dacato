package co.ecso.jdao;

import co.ecso.jdao.config.ApplicationConfig;
import co.ecso.jdao.connection.ConnectionPool;

import java.sql.Connection;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * TestApplicationConfig.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 06.09.16
 */
class TestApplicationConfig implements ApplicationConfig {
    private static snaq.db.ConnectionPool connectionPool = null;
    private static ScheduledThreadPoolExecutor threadPool = null;

    @Override
    public String getMysqlHost() {
        return null;
    }

    @Override
    public int getMysqlPort() {
        return 3306;
    }

    @Override
    public int getMaxConnections() {
        return 10;
    }

    @Override
    public String getPoolName() {
        return "dbpool";
    }

    @Override
    public int getMinPoolSize() {
        return 1;
    }

    @Override
    public int getMaxPoolSize() {
        return 10;
    }

    @Override
    public int getPoolMaxSize() {
        return 100;
    }

    @Override
    public long getPoolIdleTimeout() {
        return 1000;
    }

    @Override
    public String getConnectString() {
        return "jdbc:hsqldb:mem:jdao";
    }

    @Override
    public synchronized ScheduledExecutorService getThreadPool() {
        //noinspection SynchronizeOnNonFinalField
        if (threadPool == null) {
            threadPool = new ScheduledThreadPoolExecutor(this.getMaxPoolSize());
        }
        return threadPool;
    }

    @Override
    public synchronized ConnectionPool<Connection> getConnectionPool() {
        if (connectionPool == null) {
            connectionPool = new snaq.db.ConnectionPool(getPoolName(), getMinPoolSize(),
                    getMaxPoolSize(), getPoolMaxSize(), getPoolIdleTimeout(),
                    getConnectString(), null);
        }
        return () -> connectionPool.getConnection();
    }
}
