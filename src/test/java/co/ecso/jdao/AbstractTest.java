package co.ecso.jdao;

import co.ecso.jdao.helpers.CreateTableOnlyFilter;
import co.ecso.jdao.helpers.MysqlToHsqlMap;
import co.ecso.jdao.hsql.HsqlConnection;
import org.hsqldb.jdbc.JDBCDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * AbstractTest.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 17.03.16
 */
public abstract class AbstractTest {

    static final Cache<Object, CompletableFuture<?>> APPLICATION_CACHE = new FakeCache();

    protected static final ApplicationConfig APPLICATION_CONFIG = new ApplicationConfig() {
        @Override
        public String getMysqlPoolName() {
            return "mysqlpool";
        }

        @Override
        public int getMysqlMinPool() {
            return 1;
        }

        @Override
        public int getMysqlMaxPool() {
            return 10;
        }

        @Override
        public int getMysqlMaxSize() {
            return 10;
        }

        @Override
        public long getMysqlPoolIdleTimeout() {
            return 1000;
        }

        @Override
        public String getMysqlHost() {
            return null;
        }

        @Override
        public int getMysqlPort() {
            return 3306;
        }

        @Override
        public String getMysqlDatabase() {
            return null;
        }

        @Override
        public int getMysqlMaxConnections() {
            return 10;
        }

        @Override
        public String getMysqlUser() {
            return null;
        }

        @Override
        public String getMysqlPassword() {
            return null;
        }

        @Override
        public String getHsqlPoolName() {
            return "hsqldbpool";
        }

        @Override
        public int getHsqlMinPoolSize() {
            return 1;
        }

        @Override
        public int getHsqlMaxPoolSize() {
            return 10;
        }

        @Override
        public int getHsqLPoolMaxSize() {
            return 10;
        }

        @Override
        public long getHsqlPoolIdleTimeout() {
            return 1000;
        }

        @Override
        public String getHsqlConnectString() {
            return "jdbc:hsqldb:wpconfig.mem";
        }

        @Override
        public ScheduledExecutorService getThreadPool() {
            return new ScheduledThreadPoolExecutor(this.getMysqlMaxPool());
        }

        @Override
        public ConnectionPool getConnectionPool() {
            return () -> new snaq.db.ConnectionPool(getHsqlPoolName(), getHsqlMinPoolSize(),
                    getHsqlMaxPoolSize(), getHsqLPoolMaxSize(), getHsqlPoolIdleTimeout(),
                    getHsqlConnectString(), null).getConnection();
        }
    };

    static {
        System.setProperty("APPLICATION_CONFIG.file", "src/test/APPLICATION_CONFIG/application.conf");
    }

    /**
     * Set up database.
     */
    protected final void setUpDatabase() {
        final JDBCDataSource dataSource = this.getDataSource();
        try {
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
                // not interested
//                 e.printStackTrace();
            }
        } catch (final IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

//    protected final void setUpLogger(final TestAppender.Output output) {
//        this.addAppender(output.getWriter(), "wp");
//    }
//
//    private void updateLoggers(final Appender appender, final Configuration APPLICATION_CONFIG) {
//        for (final LoggerConfig loggerConfig : APPLICATION_CONFIG.getLoggers().values()) {
//            loggerConfig.addAppender(appender, Level.ALL, null);
//        }
//        APPLICATION_CONFIG.getRootLogger().addAppender(appender, Level.ALL, null);
//    }

//    private void addAppender(final Writer writer, final String writerName) {
//        LoggerContext context = (LoggerContext) LogManager.getContext(false);
//        final Configuration APPLICATION_CONFIG = context.getConfiguration();
//        final PatternLayout layout = PatternLayout.createDefaultLayout(APPLICATION_CONFIG);
//        final Appender appender = WriterAppender.createAppender(layout, null, writer, writerName, false, true);
//        appender.start();
//        APPLICATION_CONFIG.addAppender(appender);
//        updateLoggers(appender, APPLICATION_CONFIG);
//    }

    private JDBCDataSource getDataSource() {
        final JDBCDataSource dataSource = new JDBCDataSource();
        dataSource.setUrl("jdbc:hsqldb:wpconfig.mem");
        return dataSource;
    }

    /**
     * Clean up Database.
     */
    protected final void cleanupDatabase() throws SQLException {
        HsqlConnection database = new HsqlConnection(APPLICATION_CONFIG);
        try (final Connection connection = database.pooledConnection()) {
            try (final Statement stmt = connection.createStatement()) {
                stmt.execute("TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK");
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
