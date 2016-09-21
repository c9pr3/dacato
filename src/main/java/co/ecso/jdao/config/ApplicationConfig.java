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
public interface ApplicationConfig {

    /**
     * Database Pool name.
     *
     * @return Database pool name.
     */
    String databasePoolName();

    /**
     * Database minimum pool size.
     *
     * @return Minimum pool.
     */
    int databasePoolMin();

    /**
     * Database maximum pool size.
     *
     * @return Maximum pool.
     */
    int databasePoolMax();

    /**
     * Pool max size.
     *
     * @return Maximum pool size.
     */
    int databasePoolMaxSize();

    /**
     * Database pool/connection idle timeout.
     *
     * @return Idle timeout in milliseconds.
     */
    long databasePoolIdleTimeout();

    /**
     * Database connection string.
     *
     * @return Database connection string.
     */
    String connectionString();

    /**
     * Thread pool to use for database queries.
     *
     * @return Thread pool.
     */
    ScheduledExecutorService threadPool();

    /**
     * Database connection pool to use.
     *
     * @return Database connection pool.
     */
    ConnectionPool<Connection> databaseConnectionPool();
}
