package co.ecso.jdao;

import co.ecso.jdao.helpers.Customer;
import co.ecso.jdao.helpers.Customers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * DatabaseTableTest.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 03.09.16
 */
public final class DatabaseTableTest extends AbstractTest {

    private Customers customers = null;

    @Before
    public void setUp() throws Exception {
        this.setUpDatabase();
        this.customers = new Customers(APPLICATION_CONFIG);
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupDatabase();
    }

    @Test
    public void testAdd() throws Exception {
        final Customer newCustomer = this.customers.create("foo1", "foo2", 12345L).get();
        Assert.assertNotNull(newCustomer);
        Assert.assertEquals("foo1", newCustomer.firstName().get().resultValue());
        Assert.assertEquals("foo2", newCustomer.lastName().get().resultValue());
    }

    @Test
    public void testFindOne() throws Exception {
        final Customer newCustomer = this.customers.create("foo1", "foo2", 12345L).get();
        Assert.assertNotNull(newCustomer);

        final Customer foundCustomer = this.customers.findOne(newCustomer.primaryKey()).get();
        Assert.assertNotNull(foundCustomer);
        Assert.assertEquals("foo1", foundCustomer.firstName().get().resultValue());
        Assert.assertEquals("foo2", foundCustomer.lastName().get().resultValue());
        Assert.assertEquals(Long.valueOf(12345L), foundCustomer.number().get().resultValue());
    }

    @Test
    public void testFindOneByFirstName() throws Exception {
        final Customer newCustomer = this.customers.create("foo1", "foo2", 12345L).get();
        Assert.assertNotNull(newCustomer);

        final Customer foundCustomer = this.customers.findOneByFirstName(newCustomer.firstName().get().resultValue()).get();
        Assert.assertNotNull(foundCustomer);
        Assert.assertEquals("foo1", foundCustomer.firstName().get().resultValue());
        Assert.assertEquals("foo2", foundCustomer.lastName().get().resultValue());
        Assert.assertEquals(Long.valueOf(12345L), foundCustomer.number().get().resultValue());
    }

    @Test
    public void testFindAllByFirstName() throws Exception {
        CompletableFuture.allOf(
                this.customers.create("foo1", "foo1", 12345L),
                this.customers.create("foo1", "foo2", 12345L),
                this.customers.create("foo1", "foo3", 12345L),
                this.customers.create("foo2", "foo4", 12345L),
                this.customers.create("foo2", "foo5", 12345L)
        ).get();

        final List<Customer> foundCustomer = this.customers.findAllByFirstName("foo1").get(5, TimeUnit.SECONDS);

        Assert.assertNotNull(foundCustomer);
        Assert.assertEquals(3, foundCustomer.size());
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void testFindAll() throws Exception {
        CompletableFuture.allOf(
                this.customers.create("foo1", "foo2", 12345L),
                this.customers.create("foo1", "foo2", 12345L),
                this.customers.create("foo1", "foo2", 12345L),
                this.customers.create("foo1", "foo2", 12345L),
                this.customers.create("foo1", "foo2", 12345L)
        ).get();

        Assert.assertEquals(5, this.customers.findAll().get(5, TimeUnit.SECONDS).size());
    }

    @Test
    public void testFindOneByFirstNameAndLastName() throws Exception {
        CompletableFuture.allOf(
                this.customers.create("foo1", "foo1", 12345L),
                this.customers.create("foo1", "foo2", 12345L),
                this.customers.create("foo1", "foo3", 12345L),
                this.customers.create("foo2", "foo4", 12345L),
                this.customers.create("foo2", "foo5", 12345L)
        ).get();

        final Customer found = this.customers.findOneByFirstNameAndLastName("foo1", "foo1").get(5, TimeUnit.SECONDS);
        Assert.assertNotNull(found);
        Assert.assertEquals("foo1", found.firstName().get().resultValue());
        Assert.assertEquals("foo1", found.lastName().get().resultValue());
    }

    @Test
    public void testRemoveAll() throws Exception {
        this.customers.removeAll().get();
        this.testFindAll();
        this.customers.removeAll().get();
        Assert.assertEquals(0, this.customers.findAll().get().size());
    }

}
