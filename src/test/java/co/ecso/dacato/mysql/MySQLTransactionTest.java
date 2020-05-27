package co.ecso.dacato.mysql;

import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.database.transaction.Transaction;
import co.ecso.dacato.helpers.Customer;
import co.ecso.dacato.helpers.Customers;
import co.ecso.dacato.helpers.Products;
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
public final class MySQLTransactionTest extends AbstractMySQLTest {

    private Customers customers = null;
    private Products products;

    @Before
    public void setUp() throws Exception {
        this.setUpMySQLDatabase();
        this.customers = new Customers(new MySQLTestApplicationConfig());
        this.products = new Products(new MySQLTestApplicationConfig());
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupMySQLDatabase();
    }

    @Test
    public void testTransaction() throws Exception {
        this.customers.removeAll().get(10, TimeUnit.SECONDS);

        final Connection connection = new MySQLTestApplicationConfig().databaseConnectionPool().getConnection();
        final ConcurrentLinkedQueue<CompletableFuture<?>> queue = new ConcurrentLinkedQueue<>();
        final Transaction transaction = new Transaction() {

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
                return new MySQLTestApplicationConfig();
            }
        };
        transaction.start();

        CompletableFuture.allOf(
                this.customers.create("foo1", 12345L, transaction),
                this.products.create("100", transaction)
        ).get(10, TimeUnit.SECONDS);

        transaction.commit().get(10, TimeUnit.SECONDS);

        final List<Customer> all = this.customers.findAll().get(10, TimeUnit.SECONDS);
        Assert.assertFalse(all.isEmpty());
    }

    @Test
    public void testTransactionRollback() throws Exception {
        this.customers.removeAll().get(10, TimeUnit.SECONDS);

        Connection connection = new MySQLTestApplicationConfig().databaseConnectionPool().getConnection();
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
                return new MySQLTestApplicationConfig();
            }
        };
        transaction.start();

        CompletableFuture.allOf(
                this.customers.create("foo1", 12345L, transaction),
                this.products.create("100", transaction)
        ).get(10, TimeUnit.SECONDS);

        transaction.rollback().get(10, TimeUnit.SECONDS);

        final List<Customer> all = this.customers.findAll().get(10, TimeUnit.SECONDS);
        Assert.assertTrue(all.isEmpty());
    }

}
