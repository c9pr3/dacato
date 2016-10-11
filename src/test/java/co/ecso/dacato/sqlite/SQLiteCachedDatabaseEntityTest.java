package co.ecso.dacato.sqlite;

import co.ecso.dacato.database.query.DatabaseField;
import co.ecso.dacato.helpers.CachedCustomer;
import co.ecso.dacato.helpers.CachedCustomers;
import org.junit.*;

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
@SuppressWarnings("Duplicates")
@Ignore
public final class SQLiteCachedDatabaseEntityTest extends AbstractSQLiteTest {

    private CachedCustomer customer;

    @Before
    public void setUp() throws Exception {
        this.setUpSQLiteDatabase();
        this.customer = new CachedCustomers(new SQLiteTestApplicationConfig()).create("firstName", 1234L)
                .get(10, TimeUnit.SECONDS);
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupMySQLiteDatabase();
    }

    @Test
    public void testId() throws Exception {
        Assert.assertEquals(Long.valueOf(4L), this.customer.primaryKey());
    }

    @Test
    public void testFirstName() throws Exception {
        Assert.assertEquals("firstName", this.customer.firstName().get().resultValue());
    }

    @Test
    public void testNumber() throws Exception {
        Assert.assertEquals(Long.valueOf(1234L), this.customer.number().get().resultValue());
    }

    @Test
    public void testSave() throws Exception {
        final Map<DatabaseField<?>, Object> map = new HashMap<>();
        map.put(CachedCustomer.Fields.FIRST_NAME, "foobar1");
        final CachedCustomer newCustomer = this.customer.save(() -> map)
                .get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer);
        Assert.assertEquals(customer.primaryKey(), this.customer.primaryKey());
        Assert.assertEquals("foobar1", newCustomer.firstName().get(10, TimeUnit.SECONDS).resultValue());
        this.customer = newCustomer;
    }
}
