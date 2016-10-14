package co.ecso.dacato.cassandra.cached;

import co.ecso.dacato.database.query.DatabaseField;
import co.ecso.dacato.hsql.AbstractHSQLTest;
import co.ecso.dacato.hsql.HSQLTestApplicationConfig;
import org.junit.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * CassandraCachedDatabaseEntityTest.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 06.09.16
 */
@Ignore
public final class CassandraCachedDatabaseEntityTest extends AbstractHSQLTest {

    private CassandraCachedCustomer customer;

    @Before
    public void setUp() throws Exception {
        this.setUpHSQLDatabase();
        this.customer = new CassandraCachedCustomers(new HSQLTestApplicationConfig()).create("firstName", 1234L)
                .get(10, TimeUnit.SECONDS);
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupHSQLDatabase();
    }

    @Test
    public void testId() throws Exception {
        Assert.assertEquals(Long.valueOf(0L), this.customer.primaryKey());
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
        map.put(CassandraCachedCustomer.Fields.FIRST_NAME, "foobar1");
        final CassandraCachedCustomer newCustomer = this.customer.save(() -> map)
                .get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer);
        Assert.assertEquals(customer.primaryKey(), this.customer.primaryKey());
        Assert.assertEquals("foobar1", newCustomer.firstName().get(10, TimeUnit.SECONDS).resultValue());
        this.customer = newCustomer;
    }
}
