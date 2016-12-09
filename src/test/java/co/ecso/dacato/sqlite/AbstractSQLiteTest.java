package co.ecso.dacato.sqlite;

import co.ecso.dacato.AbstractTest;
import co.ecso.dacato.helpers.CreateTableOnlyFilter;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * AbstractSQLiteTest.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 10.10.16
 */
abstract class AbstractSQLiteTest extends AbstractTest {

    final void setUpSQLiteDatabase() throws Exception {
        final String lines = Files.readAllLines(Paths.get("test.sql"))
                .stream()
                .filter(CreateTableOnlyFilter::filter)
                .map(MysqlToSQLiteMapFilter::filter)
                .collect(Collectors.joining());
        try (final Connection connection = getSQLiteDataSource().getConnection()) {
            try (final Statement stmt = connection.createStatement()) {
                final String[] splittedLines = lines.split(";");
                for (final String line : splittedLines) {
//                    System.out.println("EXECUTING: " + line);
                    stmt.execute(line);
                }
            }
        } catch (final SQLException ignored) {
//            ignored.printStackTrace();
            //ignored
        }
    }

    private DataSource getSQLiteDataSource() {
        final SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:./test.sqlite");
        return dataSource;
    }

    protected final void cleanupMySQLiteDatabase() throws SQLException {
        try (final Connection connection = getSQLiteDataSource().getConnection()) {
            try (final Statement stmt = connection.createStatement()) {
                stmt.execute("DELETE FROM customer");
                stmt.execute("DELETE FROM products");
            }
        }
        CACHE.invalidateAll();
        CACHE.cleanUp();
    }

}
