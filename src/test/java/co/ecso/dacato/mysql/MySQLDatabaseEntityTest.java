package co.ecso.dacato.mysql;

import co.ecso.dacato.database.query.DatabaseField;
import co.ecso.dacato.helpers.Customer;
import co.ecso.dacato.helpers.Customers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * MySQLDatabaseEntityTest.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 03.09.16
 */
public final class MySQLDatabaseEntityTest extends AbstractMySQLTest {

    private Customer customer = null;

    @Before
    public void setUp() throws Exception {
        this.setUpMySQLDatabase();
        this.customer = new Customers(new MySQLTestApplicationConfig()).create("firstName", 1234L)
                .get(10, TimeUnit.SECONDS);
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupMySQLDatabase();
    }

    @Test
    public void testId() throws Exception {
        Assert.assertEquals(Long.valueOf(4L), this.customer.primaryKey());
    }

    @Test
    public void testFirstName() throws Exception {
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

        this.customer = new Customers(new MySQLTestApplicationConfig()).findOne(id).get(10, TimeUnit.SECONDS);
        Assert.assertEquals("foo1", this.customer.firstName().get(10, TimeUnit.SECONDS).resultValue());

        map.clear();
        map.put(Customer.Fields.FIRST_NAME, "foo2");
        this.customer.save(() -> map).get(5, TimeUnit.SECONDS);

        Assert.assertEquals("foo2", this.customer.firstName().get(10, TimeUnit.SECONDS).resultValue());
    }
}
