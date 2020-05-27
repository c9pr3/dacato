package co.ecso.dacato.postgresql;

import co.ecso.dacato.AbstractTest;
import co.ecso.dacato.helpers.CreateTableOnlyFilter;
import de.flapdoodle.embed.process.distribution.GenericVersion;
import de.flapdoodle.embed.process.distribution.IVersion;
import org.postgresql.jdbc3.Jdbc3PoolingDataSource;
import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * AbstractPSQLTest.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @version $Id:$
 * @since 08.10.16
 */
abstract class AbstractPSQLTest extends AbstractTest {

    private static final Logger LOGGER = Logger.getLogger(AbstractPSQLTest.class.getName());
    private static final PostgresProcess POSTGRES_PROCESS;
    private static final String POSTGRE_SQLURL;

    static {
        try {
            // starting Postgres
            final PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getDefaultInstance();
            final IVersion version = new GenericVersion("9.5.0-1");
            final PostgresConfig config = new PostgresConfig(version, "127.0.0.1", 50776, "testDB");
            config.getAdditionalInitDbParams().addAll(Arrays.asList(
                    "-E", "UTF-8",
                    "--locale=en_US.UTF-8",
                    "--lc-collate=en_US.UTF-8",
                    "--lc-ctype=en_US.UTF-8"
            ));
            final PostgresExecutable exec = runtime.prepare(config);
            POSTGRES_PROCESS = exec.start();

            // connecting to a running Postgres
            POSTGRE_SQLURL = String.format("jdbc:postgresql://%s:%s/%s",
                    config.net().host(),
                    config.net().port(),
                    config.storage().dbName()
            );
        } catch (final Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    final void setUpPSQLDatabase() throws IOException {
        final String lines = Files.readAllLines(Paths.get("test.sql"))
                .stream()
                .filter(CreateTableOnlyFilter::filter)
                .map(MysqlToPsqlMapFilter::filter)
                .collect(Collectors.joining());
        try (final Connection connection = getPostgreSQLDataSource().getConnection()) {
            try (final Statement stmt = connection.createStatement()) {
                stmt.execute("CREATE DATABASE server_v5");
                stmt.execute(lines);
            }
        } catch (final SQLException e) {
            LOGGER.warning(e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private Jdbc3PoolingDataSource getPostgreSQLDataSource() {
        final Jdbc3PoolingDataSource dataSource = new Jdbc3PoolingDataSource();
        dataSource.setUrl(POSTGRE_SQLURL);
        return dataSource;
    }

    final void cleanupPostgreSQLDatabase() {
        try (final Connection connection = this.getPostgreSQLDataSource().getConnection()) {
            try (final Statement stmt = connection.createStatement()) {
                stmt.execute("DROP TABLE customer");
                stmt.execute("DROP TABLE products");
                stmt.execute("DROP DATABASE server_v5");
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        CACHE.invalidateAll();
        CACHE.cleanUp();
    }

}
