package co.ecso.dacato.mysql;

import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import co.ecso.dacato.AbstractTest;
import co.ecso.dacato.helpers.CreateTableOnlyFilter;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * AbstractMySQLTest.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @version $Id:$
 * @since 10.10.16
 */
abstract class AbstractMySQLTest extends AbstractTest {

    private static final Logger LOGGER = Logger.getLogger(AbstractMySQLTest.class.getName());

    static {
        DBConfigurationBuilder configBuilder = DBConfigurationBuilder.newBuilder();
        configBuilder.setPort(3399);
        configBuilder.setDataDir("src/test/conf/");
        try {
            DB db = DB.newEmbeddedDB(configBuilder.build());
            db.start();
        } catch (final Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    final void setUpMySQLDatabase() throws Exception {
        final String lines = Files.readAllLines(Paths.get("test.sql"))
                .stream()
                .filter(CreateTableOnlyFilter::filter)
                .collect(Collectors.joining());
        try (final Connection connection = getMySQLDataSource().getConnection()) {
            try (final Statement stmt = connection.createStatement()) {
                stmt.execute("CREATE DATABASE server_v5");
                stmt.execute("USE server_v5");
                final String[] splittedLines = lines.split(";");
                for (final String line : splittedLines) {
                    stmt.execute(line);
                }
            }
        } catch (final SQLException e) {
            LOGGER.warning(e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private DataSource getMySQLDataSource() {
        final MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3399/?user=root");
        return dataSource;
    }

    final void cleanupMySQLDatabase() {
        try (final Connection connection = getMySQLDataSource().getConnection()) {
            try (final Statement stmt = connection.createStatement()) {
                stmt.execute("DROP DATABASE server_v5");
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        CACHE.invalidateAll();
        CACHE.cleanUp();
    }

}
