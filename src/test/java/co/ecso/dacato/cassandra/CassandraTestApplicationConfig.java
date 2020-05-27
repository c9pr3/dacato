package co.ecso.dacato.cassandra;

import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.connection.ConnectionPool;

import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RedisTestApplicationConfig.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @version $Id:$
 * @since 06.09.16
 */
final class CassandraTestApplicationConfig implements ApplicationConfig {
    private static volatile snaq.db.ConnectionPool connectionPool = null;
    private static volatile ExecutorService threadPool = null;

    @Override
    public String databasePoolName() {
        return "dbpoolhcassandra";
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
        return 10000;
    }

    @Override
    public String connectionString() {
        try {
            Class.forName("org.apache.cassandra.cql.jdbc.CassandraDriver");
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return "jdbc:cassandra://127.0.0.1:9171/otherKeyspaceName?primarydc=DC1&backupdc=DC2&consistency=QUORUM";
    }

    @Override
    public ExecutorService threadPool() {
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
