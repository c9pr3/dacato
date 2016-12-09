package co.ecso.dacato.sqlite;

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
 * @author Christian Senkowski (cs@2scale.net)
 * @since 08.12.16
 */
public final class SQLiteTransactionTest extends AbstractSQLiteTest {

    private SQLiteCustomers customers = null;
    private SQLiteProducts products;

    @Before
    public void setUp() throws Exception {
        this.setUpSQLiteDatabase();
        this.customers = new SQLiteCustomers(new SQLiteTestApplicationConfig());
        this.products = new SQLiteProducts(new SQLiteTestApplicationConfig());
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupMySQLiteDatabase();
    }

    @Test
    public void testTransaction() throws Exception {
        this.customers.removeAll().get(10, TimeUnit.SECONDS);

        Connection connection = new SQLiteTestApplicationConfig().databaseConnectionPool().getConnection();
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
                return new SQLiteTestApplicationConfig();
            }
        };
        transaction.start();

        CompletableFuture.allOf(
                this.customers.create("foo1", 12345, transaction),
                this.products.create("100", transaction)
        ).get(10, TimeUnit.SECONDS);

        transaction.commit().get(10, TimeUnit.SECONDS);

        final List<SQLiteCustomer> all = this.customers.findAll().get(10, TimeUnit.SECONDS);
        Assert.assertFalse(all.isEmpty());
    }

    @Test
    public void testTransactionRollback() throws Exception {
        this.customers.removeAll().get(10, TimeUnit.SECONDS);

        System.out.println("MOVE ON");

        Connection connection = new SQLiteTestApplicationConfig().databaseConnectionPool().getConnection();
        Assert.assertNotNull(connection);
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
                return new SQLiteTestApplicationConfig();
            }
        };
        transaction.start();

        CompletableFuture.allOf(
                this.customers.create("foo1", 12345, transaction),
                this.products.create("100", transaction)
        ).get(10, TimeUnit.SECONDS);

        transaction.rollback().get(10, TimeUnit.SECONDS);

        final List<SQLiteCustomer> all = this.customers.findAll().get(10, TimeUnit.SECONDS);
        Assert.assertTrue(all.isEmpty());
    }

}
