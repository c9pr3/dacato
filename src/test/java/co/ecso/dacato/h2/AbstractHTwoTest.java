package co.ecso.dacato.h2;

import co.ecso.dacato.AbstractTest;
import co.ecso.dacato.helpers.CreateTableOnlyFilter;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * AbstractHTwoTest.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @version $Id:$
 * @since 10.10.16
 */
abstract class AbstractHTwoTest extends AbstractTest {

    private static final Logger LOGGER = Logger.getLogger(AbstractHTwoTest.class.getName());

    static {
        try {
            Server.createTcpServer().start();
        } catch (final SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    final void setUpHTwoDatabase() throws Exception {
        final String lines = Files.readAllLines(Paths.get("test.sql"))
                .stream()
                .filter(CreateTableOnlyFilter::filter)
                .collect(Collectors.joining());
        try (final Connection connection = getHTwoDataSource().getConnection()) {
            try (final Statement stmt = connection.createStatement()) {
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

    private DataSource getHTwoDataSource() {
        try {
            Class.forName("org.h2.Driver");
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        final JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:tcp://localhost/~/test");
        return dataSource;
    }

    final void cleanupHTwoDatabase() {
        try (final Connection connection = getHTwoDataSource().getConnection()) {
            try (final Statement stmt = connection.createStatement()) {
                stmt.execute("DROP TABLE customer");
                stmt.execute("DROP TABLE products");
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        CACHE.invalidateAll();
        CACHE.cleanUp();
    }

}
