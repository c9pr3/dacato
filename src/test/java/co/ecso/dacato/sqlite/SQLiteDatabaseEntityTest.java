package co.ecso.dacato.sqlite;

import co.ecso.dacato.database.query.DatabaseField;
import co.ecso.dacato.database.query.DatabaseResultField;
import co.ecso.dacato.database.query.SingleColumnQuery;
import co.ecso.dacato.helpers.Customer;
import co.ecso.dacato.helpers.Customers;
import org.junit.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SQLiteDatabaseEntityTest.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 03.09.16
 */
@Ignore
public final class SQLiteDatabaseEntityTest extends AbstractSQLiteTest {

    private Customer customer = null;

    @Before
    public void setUp() throws Exception {
        this.setUpSQLiteDatabase();
        this.customer = new SQLiteCustomers(new SQLiteTestApplicationConfig()).create("firstName", 1234L)
                .get(10, TimeUnit.SECONDS);
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupMySQLiteDatabase();
    }

    @Test
    public void testId() throws Exception {
        Assert.assertEquals(Long.valueOf(1L), this.customer.primaryKey());
    }

    @Test
    public void testFirstName() throws Exception {
        this.customer = new SQLiteCustomers(new SQLiteTestApplicationConfig()).create("firstName", 1234L)
                .get(10, TimeUnit.SECONDS);
        Assert.assertEquals(Long.valueOf(2L), this.customer.primaryKey());
        final DatabaseResultField<Long> res = this.customer.findOne(new SingleColumnQuery<>(
                "SELECT %s FROM customer WHERE %s = ?", Customer.Fields.ID, Customer.Fields.ID, 2L), () ->
                new AtomicBoolean(true)).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(res);
        Assert.assertEquals(Long.valueOf(1L), res.resultValue());

        Assert.assertEquals("firstName", this.customer.firstName().get(10, TimeUnit.SECONDS).resultValue());
    }

    @Test
    public void testNumber() throws Exception {
        Assert.assertEquals(Long.valueOf(1234), this.customer.number().get(10, TimeUnit.SECONDS).resultValue());
    }

    @Test(expected = ExecutionException.class)
    public void testSave() throws Exception {
        final Long id = this.customer.primaryKey();
        Map<DatabaseField<?>, Object> map = new HashMap<>();
        map.put(Customer.Fields.FIRST_NAME, "foo1");
        this.customer.save(() -> map).get(5, TimeUnit.SECONDS);

        this.customer = new Customers(new SQLiteTestApplicationConfig()).findOne(id).get(10, TimeUnit.SECONDS);
        Assert.assertEquals("foo1", this.customer.firstName().get(10, TimeUnit.SECONDS).resultValue());

        map.clear();
        map.put(Customer.Fields.FIRST_NAME, "foo2");
        this.customer.save(() -> map).get(5, TimeUnit.SECONDS);

        Assert.assertEquals("foo2", this.customer.firstName().get(10, TimeUnit.SECONDS).resultValue());
    }
}
