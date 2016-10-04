package co.ecso.dacato;

import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.connection.ConnectionPool;
import co.ecso.dacato.database.cache.Cache;
import co.ecso.dacato.database.cache.CacheKey;
import co.ecso.dacato.helpers.CreateTableOnlyFilter;
import co.ecso.dacato.helpers.MysqlToHsqlMapFilter;
import org.hsqldb.jdbc.JDBCDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * AbstractTest.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 17.03.16
 */
public abstract class AbstractTest {

    static {
        System.setProperty("config.file", "src/test/config/application.conf");
    }

    public static final Cache<CacheKey, CompletableFuture> CACHE = new TestApplicationCache<>();
    static final ApplicationConfig APPLICATION_CONFIG = new TestApplicationConfig();
    private static final Logger LOGGER = Logger.getLogger(AbstractTest.class.getName());

    /**
     * Set up database.
     */
    protected final void setUpDatabase() throws IOException {
        final JDBCDataSource dataSource = this.getDataSource();
        final String lines = Files.readAllLines(Paths.get("test.sql"))
                .stream()
                .filter(CreateTableOnlyFilter::filter)
                .map(MysqlToHsqlMapFilter::filter)
                .collect(Collectors.joining());
        try (final Connection connection = dataSource.getConnection()) {
            try (final Statement stmt = connection.createStatement()) {
                stmt.execute(lines);
            }
        } catch (final SQLException e) {
            e.printStackTrace();
            LOGGER.warning(e.getMessage());
        }
    }

    private JDBCDataSource getDataSource() {
        final JDBCDataSource dataSource = new JDBCDataSource();
        dataSource.setUrl("jdbc:hsqldb:mem:dacato");
        return dataSource;
    }

    /**
     * Clean up Database.
     */
    protected final void cleanupDatabase() throws SQLException {
        final ConnectionPool<Connection> connectionPool = new TestApplicationConfig().databaseConnectionPool();
        try (final Connection connection = connectionPool.getConnection()) {
            try (final Statement stmt = connection.createStatement()) {
                stmt.execute("TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK");
            }
        } catch (final SQLException e) {
//            throw new RuntimeException(e.getMessage(), e);
        }
        CACHE.invalidateAll();
        CACHE.cleanUp();
    }

}
