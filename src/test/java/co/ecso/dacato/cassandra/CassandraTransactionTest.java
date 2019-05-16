package co.ecso.dacato.cassandra;

import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.database.transaction.Transaction;
import java.sql.Connection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * HTwoTransactionTest.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @since 08.12.16
 */
@Ignore
public final class CassandraTransactionTest extends AbstractCassandraTest {

/*
    @Rule
    public final CassandraUnit cassandraUnit = new CassandraUnit(
            new AbstractXmlDataSet(SRC_TEST_CONFIG_EXTENDED_DATA_SET_XML) {
                @Override
                protected InputStream getInputDataSetLocation(final String s) {
                    try {
                        return new FileInputStream(SRC_TEST_CONFIG_EXTENDED_DATA_SET_XML);
                    } catch (final FileNotFoundException e) {
                        return null;
                    }
                }
            });
*/

    private CassandraCustomers customers = null;
    private CassandraProducts products;

    @Before
    public void setUp() throws Exception {
        this.setupCassandraDatabase();
        this.customers = new CassandraCustomers(new CassandraTestApplicationConfig());
        this.products = new CassandraProducts(new CassandraTestApplicationConfig());
    }

    @After
    public void tearDown() {
        this.cleanupCassandraDatabase();
    }

    @Test
    public void testTransaction() throws Exception {
        this.customers.removeAll().get(10, TimeUnit.SECONDS);

        Connection connection = new CassandraTestApplicationConfig().databaseConnectionPool().getConnection();
        ConcurrentLinkedQueue<CompletableFuture<?>> queue = new ConcurrentLinkedQueue<>();
        Transaction transaction = new Transaction() {
            @Override
            public Connection connection() {
                return connection;
            }

            @Override
            public ConcurrentLinkedQueue<CompletableFuture<?>> futures() {
                return queue;
            }

            @Override
            public ApplicationConfig config() {
                return new CassandraTestApplicationConfig();
            }
        };
        transaction.start();

        CompletableFuture.allOf(
                this.customers.create("foo1", 12345L, transaction),
                this.products.create("100", transaction)
        ).get(10, TimeUnit.SECONDS);

        transaction.commit().get(10, TimeUnit.SECONDS);

        final List<CassandraCustomer> all = this.customers.findAll().get(10, TimeUnit.SECONDS);
        Assert.assertFalse(all.isEmpty());
    }

    @Test
    public void testTransactionRollback() throws Exception {
        this.customers.removeAll().get(10, TimeUnit.SECONDS);

        Connection connection = new CassandraTestApplicationConfig().databaseConnectionPool().getConnection();
        Assert.assertNotNull(connection);
        ConcurrentLinkedQueue<CompletableFuture<?>> queue = new ConcurrentLinkedQueue<>();
        Transaction transaction = new Transaction() {

            @Override
            public Connection connection() {
                return connection;
            }

            @Override
            public ConcurrentLinkedQueue<CompletableFuture<?>> futures() {
                return queue;
            }

            @Override
            public ApplicationConfig config() {
                return new CassandraTestApplicationConfig();
            }
        };
        transaction.start();

        CompletableFuture.allOf(
                this.customers.create("foo1", 12345L, transaction),
                this.products.create("100", transaction)
        ).get(10, TimeUnit.SECONDS);

        transaction.rollback().get(10, TimeUnit.SECONDS);

        final List<CassandraCustomer> all = this.customers.findAll().get(10, TimeUnit.SECONDS);
        Assert.assertTrue(all.isEmpty());

    }

}
