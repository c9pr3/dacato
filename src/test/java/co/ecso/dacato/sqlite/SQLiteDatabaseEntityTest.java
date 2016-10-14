package co.ecso.dacato.sqlite;

import co.ecso.dacato.database.query.DatabaseField;
import co.ecso.dacato.database.query.DatabaseResultField;
import co.ecso.dacato.database.query.SingleColumnQuery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
public final class SQLiteDatabaseEntityTest extends AbstractSQLiteTest {

    private SQLiteCustomer customer = null;

    @Before
    public void setUp() throws Exception {
        this.setUpSQLiteDatabase();
        this.customer = new SQLiteCustomers(new SQLiteTestApplicationConfig()).create("firstName", 1234)
                .get(10, TimeUnit.SECONDS);
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupMySQLiteDatabase();
    }

    @Test
    public void testId() throws Exception {
        Assert.assertEquals(Integer.valueOf(1), this.customer.primaryKey());
    }

    @Test
    public void testFirstName() throws Exception {
        this.customer = new SQLiteCustomers(new SQLiteTestApplicationConfig()).create("firstName", 1234)
                .get(10, TimeUnit.SECONDS);
        Assert.assertEquals(Integer.valueOf(2), this.customer.primaryKey());
        final DatabaseResultField<Integer> res = this.customer.findOne(new SingleColumnQuery<>(
                "SELECT %s FROM customer WHERE %s = ?", SQLiteCustomer.Fields.ID, SQLiteCustomer.Fields.ID, 2), () ->
                new AtomicBoolean(true)).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(res);
        Assert.assertEquals(Integer.valueOf(2), res.resultValue());

        Assert.assertEquals("firstName", this.customer.firstName().get(10, TimeUnit.SECONDS).resultValue());
    }

    @Test
    public void testNumber() throws Exception {
        Assert.assertEquals(Integer.valueOf(1234), this.customer.number().get(10, TimeUnit.SECONDS).resultValue());
    }

    @Test(expected = ExecutionException.class)
    public void testSave() throws Exception {
        final Integer id = this.customer.primaryKey();
        Map<DatabaseField<?>, Object> map = new HashMap<>();
        map.put(SQLiteCustomer.Fields.FIRST_NAME, "foo1");
        this.customer.save(() -> map).get(5, TimeUnit.SECONDS);

        this.customer = new SQLiteCustomers(new SQLiteTestApplicationConfig()).findOne(id).get(10, TimeUnit.SECONDS);
        Assert.assertEquals("foo1", this.customer.firstName().get(10, TimeUnit.SECONDS).resultValue());

        map.clear();
        map.put(SQLiteCustomer.Fields.FIRST_NAME, "foo2");
        this.customer.save(() -> map).get(5, TimeUnit.SECONDS);

        Assert.assertEquals("foo2", this.customer.firstName().get(10, TimeUnit.SECONDS).resultValue());
    }
}
