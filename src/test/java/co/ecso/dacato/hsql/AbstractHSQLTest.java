package co.ecso.dacato.hsql;

import co.ecso.dacato.AbstractTest;
import co.ecso.dacato.helpers.CreateTableOnlyFilter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * AbstractHSQLTest.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 08.10.16
 */
public abstract class AbstractHSQLTest extends AbstractTest {

//    private static final Logger LOGGER = Logger.getLogger(AbstractHSQLTest.class.getName());

    protected final void setUpHSQLDatabase() throws IOException {
        final String lines = Files.readAllLines(Paths.get("test.sql"))
                .stream()
                .filter(CreateTableOnlyFilter::filter)
                .map(MysqlToHsqlMapFilter::filter)
                .collect(Collectors.joining());
        try (final Connection connection = getHSQLDataSource().getConnection()) {
            try (final Statement stmt = connection.createStatement()) {
                stmt.execute("CREATE SCHEMA server_v5;");
//                System.out.println("EXECUTING " + lines);
                stmt.execute(lines);
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private org.hsqldb.jdbc.JDBCDataSource getHSQLDataSource() {
        final org.hsqldb.jdbc.JDBCDataSource dataSource = new org.hsqldb.jdbc.JDBCDataSource();
        dataSource.setUrl("jdbc:hsqldb:mem:dacato");
        return dataSource;
    }

    /**
     * Clean up Database.
     */
    protected final void cleanupHSQLDatabase() {
        try (final Connection connection = this.getHSQLDataSource().getConnection()) {
            try (final Statement stmt = connection.createStatement()) {
                stmt.execute("DROP SCHEMA server_v5 CASCADE");
                stmt.execute("DROP SCHEMA PUBLIC CASCADE");
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        CACHE.invalidateAll();
        CACHE.cleanUp();
    }

}
