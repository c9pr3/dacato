package co.ecso.jdao;

import co.ecso.jdao.config.ApplicationConfig;
import co.ecso.jdao.database.cache.Cache;
import co.ecso.jdao.database.cache.CacheKey;
import co.ecso.jdao.helpers.CreateTableOnlyFilter;
import co.ecso.jdao.helpers.MysqlToHsqlMap;
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

    private static final Logger LOGGER = Logger.getLogger(AbstractTest.class.getName());
    static final ApplicationConfig APPLICATION_CONFIG = new TestApplicationConfig();
    public static final Cache<CacheKey<?>, CompletableFuture<?>> CACHE = new TestApplicationCache<>();

    static {
        System.setProperty("APPLICATION_CONFIG.file", "src/test/APPLICATION_CONFIG/application.conf");
    }

    /**
     * Set up database.
     */
    protected final void setUpDatabase() throws IOException {
        final JDBCDataSource dataSource = this.getDataSource();
        final String lines = Files.readAllLines(Paths.get("test.sql"))
                .stream()
                .filter(CreateTableOnlyFilter::filter)
                .map(MysqlToHsqlMap::filter)
                .collect(Collectors.joining());
        try (final Connection connection = dataSource.getConnection()) {
            try (final Statement stmt = connection.createStatement()) {
                stmt.execute(lines);
            }
        } catch (final SQLException e) {
            LOGGER.warning(e.getMessage());
        }
    }

    private JDBCDataSource getDataSource() {
        final JDBCDataSource dataSource = new JDBCDataSource();
        dataSource.setUrl("jdbc:hsqldb:mem:jdao");
        return dataSource;
    }

    /**
     * Clean up Database.
     */
    protected final void cleanupDatabase() throws SQLException {
        try (final Connection connection = APPLICATION_CONFIG.databaseConnectionPool().getConnection()) {
            try (final Statement stmt = connection.createStatement()) {
                stmt.execute("TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK");
            }
        }
        CACHE.invalidateAll();
        CACHE.cleanUp();
    }

}
