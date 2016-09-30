package co.ecso.jdao;

import co.ecso.jdao.config.ApplicationConfig;
import co.ecso.jdao.connection.ConnectionPool;

import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TestApplicationConfig.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 06.09.16
 */
final class TestApplicationConfig implements ApplicationConfig {
    private static snaq.db.ConnectionPool connectionPool = null;
    private static ExecutorService threadPool = null;

    @Override
    public String databasePoolName() {
        return "dbpool";
    }

    @Override
    public int databasePoolMin() {
        return 1;
    }

    @Override
    public int databasePoolMax() {
        return 10;
    }

    @Override
    public int databasePoolMaxSize() {
        return 100;
    }

    @Override
    public long databasePoolIdleTimeout() {
        return 1000;
    }

    @Override
    public String connectionString() {
        return "jdbc:hsqldb:mem:jdao";
//        return "jdbc:mysql://localhost:3306/server_v5?user=root&password=test";
    }

    @Override
    public synchronized ExecutorService threadPool() {
        if (threadPool == null) {
            threadPool = Executors.newCachedThreadPool();

        }
        return threadPool;
    }

    @Override
    public synchronized ConnectionPool<Connection> databaseConnectionPool() {
        if (connectionPool == null) {
            connectionPool = new snaq.db.ConnectionPool(databasePoolName(), databasePoolMin(),
                    databasePoolMax(), databasePoolMaxSize(), databasePoolIdleTimeout(),
                    connectionString(), null);
        }
        return () -> connectionPool.getConnection();
    }

}
