package co.ecso.jdao;

import co.ecso.jdao.database.query.DatabaseField;
import co.ecso.jdao.helpers.Customer;
import co.ecso.jdao.helpers.Customers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * DatabaseEntityTest.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 03.09.16
 */
public final class DatabaseEntityTest extends AbstractTest {

    private Customer customer = null;

    @Before
    public void setUp() throws Exception {
        this.setUpDatabase();
        this.customer = new Customers(APPLICATION_CONFIG).create("firstName", "lastName", 1234L).get();
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupDatabase();
    }

    @Test
    public void testId() throws Exception {
        Assert.assertEquals(Long.valueOf(0L), this.customer.id());
    }

    @Test
    public void testFirstName() throws Exception {
        Assert.assertEquals("firstName", this.customer.firstName().get().value());
    }

    @Test
    public void testLastName() throws Exception {
        Assert.assertEquals("lastName", this.customer.lastName().get().value());
    }

    @Test
    public void testNumber() throws Exception {
        Assert.assertEquals(Long.valueOf(1234L), this.customer.number().get().value());
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testSave() throws Exception {
        final Long id = this.customer.id();
        this.customer.save(() -> new HashMap<DatabaseField<?>, Object>() {
            {
                put(Customer.Fields.FIRST_NAME, "foo1");
                put(Customer.Fields.LAST_NAME, "bla1");
            }
        }).get(5, TimeUnit.SECONDS);

        this.customer = new Customers(APPLICATION_CONFIG).findOne(id).get();
        Assert.assertEquals("foo1", this.customer.firstName().get().value());
        Assert.assertEquals("bla1", this.customer.lastName().get().value());

        this.customer.save(() -> new HashMap<DatabaseField<?>, Object>() {
            {
                put(Customer.Fields.FIRST_NAME, "foo2");
            }
        }).get(5, TimeUnit.SECONDS);

        Assert.assertEquals("foo2", this.customer.firstName().get().value());
    }
}
