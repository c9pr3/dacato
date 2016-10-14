package co.ecso.dacato.sqlite.cached;

import co.ecso.dacato.database.query.DatabaseField;
import co.ecso.dacato.sqlite.AbstractSQLiteTest;
import co.ecso.dacato.sqlite.SQLiteTestApplicationConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * SQLiteCachedDatabaseEntityTest.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 06.09.16
 */
public final class SQLiteCachedDatabaseEntityTest extends AbstractSQLiteTest {

    private SQLiteCachedCustomer customer;

    @Before
    public void setUp() throws Exception {
        this.setUpSQLiteDatabase();
        this.customer = new SQLiteCachedCustomers(new SQLiteTestApplicationConfig()).create("firstName", 1234)
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
        Assert.assertEquals("firstName", this.customer.firstName().get().resultValue());
    }

    @Test
    public void testNumber() throws Exception {
        Assert.assertEquals(Integer.valueOf(1234), this.customer.number().get().resultValue());
    }

    @Test
    public void testSave() throws Exception {
        final Map<DatabaseField<?>, Object> map = new HashMap<>();
        map.put(SQLiteCachedCustomer.Fields.FIRST_NAME, "foobar1");
        final SQLiteCachedCustomer newCustomer = this.customer.save(() -> map)
                .get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer);
        Assert.assertEquals(customer.primaryKey(), this.customer.primaryKey());
        Assert.assertEquals("foobar1", newCustomer.firstName().get(10, TimeUnit.SECONDS).resultValue());
        this.customer = newCustomer;
    }
}
