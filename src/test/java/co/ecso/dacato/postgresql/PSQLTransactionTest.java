package co.ecso.dacato.postgresql;

import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.database.transaction.Transaction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * HTwoTransactionTest.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @since 08.12.16
 */
public final class PSQLTransactionTest extends AbstractPSQLTest {

    private PSQLCustomers customers = null;
    private PSQLProducts products;

    @Before
    public void setUp() throws Exception {
        this.setUpPSQLDatabase();
        this.customers = new PSQLCustomers(new PSQLTestApplicationConfig());
        this.products = new PSQLProducts(new PSQLTestApplicationConfig());
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupPostgreSQLDatabase();
    }

    @Test
    public void testTransaction() throws Exception {
        this.customers.removeAll().get(10, TimeUnit.SECONDS);

        Connection connection = new PSQLTestApplicationConfig().databaseConnectionPool().getConnection();
        ConcurrentLinkedQueue<CompletableFuture<?>> queue = new ConcurrentLinkedQueue<>();
        Transaction transaction = new Transaction() {
            @Override
            public Connection connection() throws SQLException {
                return connection;
            }

            @Override
            public ConcurrentLinkedQueue<CompletableFuture<?>> futures() {
                return queue;
            }

            @Override
            public ApplicationConfig config() {
                return new PSQLTestApplicationConfig();
            }
        };
        transaction.start();

        CompletableFuture.allOf(
                this.customers.create("foo1", 12345L, transaction),
                this.products.create("100", transaction)
        ).get(10, TimeUnit.SECONDS);

        transaction.commit().get(10, TimeUnit.SECONDS);

        final List<PSQLCustomer> all = this.customers.findAll().get(10, TimeUnit.SECONDS);
        Assert.assertFalse(all.isEmpty());

    }

    @Test
    public void testTransactionRollback() throws Exception {
        this.customers.removeAll().get(10, TimeUnit.SECONDS);

        Connection connection = new PSQLTestApplicationConfig().databaseConnectionPool().getConnection();
        ConcurrentLinkedQueue<CompletableFuture<?>> queue = new ConcurrentLinkedQueue<>();
        Transaction transaction = new Transaction() {

            @Override
            public Connection connection() throws SQLException {
                return connection;
            }

            @Override
            public ConcurrentLinkedQueue<CompletableFuture<?>> futures() {
                return queue;
            }

            @Override
            public ApplicationConfig config() {
                return new PSQLTestApplicationConfig();
            }
        };
        transaction.start();

        CompletableFuture.allOf(
                this.customers.create("foo1", 12345L, transaction),
                this.products.create("100", transaction)
        ).get(10, TimeUnit.SECONDS);

        transaction.rollback().get(10, TimeUnit.SECONDS);

        final List<PSQLCustomer> all = this.customers.findAll().get(10, TimeUnit.SECONDS);
        Assert.assertTrue(all.isEmpty());

    }

}
