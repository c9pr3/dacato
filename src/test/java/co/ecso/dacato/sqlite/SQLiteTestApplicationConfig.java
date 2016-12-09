package co.ecso.dacato.sqlite;

import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.connection.ConnectionPool;

import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * SQLiteTestApplicationConfig.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 06.09.16
 */
final class SQLiteTestApplicationConfig implements ApplicationConfig {
    private static volatile snaq.db.ConnectionPool connectionPool = null;
    private static volatile ExecutorService threadPool = null;

    @Override
    public String databasePoolName() {
        return "dbpoolsqlite";
    }

    @Override
    public int databasePoolMin() {
        return 1;
    }

    @Override
    public int databasePoolMax() {
        return 1;
    }

    @Override
    public int databasePoolMaxSize() {
        return 1;
    }

    @Override
    public long databasePoolIdleTimeout() {
        return 100;
    }

    @Override
    public String connectionString() {
        return "jdbc:sqlite:./test.sqlite";
    }

    @Override
    public ExecutorService threadPool() {
        if (threadPool == null) {
            threadPool = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>());
        }
        return threadPool;
    }

    @Override
    public ConnectionPool<Connection> databaseConnectionPool() {
        if (connectionPool == null) {
            connectionPool = new snaq.db.ConnectionPool(databasePoolName(), databasePoolMin(),
                    databasePoolMax(), databasePoolMaxSize(), databasePoolIdleTimeout(),
                    connectionString(), null);
        }
        return () -> connectionPool.getConnection();
    }

}
