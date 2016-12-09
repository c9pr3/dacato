package co.ecso.dacato.cassandra;

import co.ecso.dacato.AbstractTest;
import co.ecso.dacato.helpers.CreateTableOnlyFilter;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * AbstractCassandraTest.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 08.10.16
 */
abstract class AbstractCassandraTest extends AbstractTest {

    static final String SRC_TEST_CONFIG_EXTENDED_DATA_SET_XML = "src/test/config/extendedDataSet.xml";
    private static final Logger LOGGER = Logger.getLogger(AbstractCassandraTest.class.getName());

    static {
        System.setProperty("cassandra.storagedir", "src/test/conf/");
        try {
            EmbeddedCassandraServerHelper.startEmbeddedCassandra();
        } catch (final TTransportException | ConfigurationException | InterruptedException | IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    final void setupCassandraDatabase() throws Exception {
        final CassandraTestApplicationConfig config = new CassandraTestApplicationConfig();
        final List<String> lines = Files.readAllLines(Paths.get("test_cassandra.sql"))
                .stream()
                .filter(CreateTableOnlyFilter::filter)
                .collect(Collectors.toList());
        try (final Connection connection = config.databaseConnectionPool().getConnection()) {
            try (final Statement stmt = connection.createStatement()) {
                for (final String line : lines) {
                    stmt.execute(MysqlToCassandraMapFilter.filter(line));
                }
            }
        } catch (final SQLException e) {
            LOGGER.warning(e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    final void cleanupCassandraDatabase() {
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
        CACHE.invalidateAll();
        CACHE.cleanUp();
    }

}
