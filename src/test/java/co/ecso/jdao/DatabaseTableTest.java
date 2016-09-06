package co.ecso.jdao;

import co.ecso.jdao.helpers.Customer;
import co.ecso.jdao.helpers.Customers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
        final Customer newCustomer = this.customers.add("foo1", "foo2", 12345L).get();
        Assert.assertNotNull(newCustomer);
        Assert.assertEquals("foo1", newCustomer.firstName().get());
        Assert.assertEquals("foo2", newCustomer.lastName().get());
    }

    @Test
    public void testFindOne() throws Exception {
        final Customer newCustomer = this.customers.add("foo1", "foo2", 12345L).get();
        Assert.assertNotNull(newCustomer);

        final Customer foundCustomer = this.customers.findOne(CompletableFuture.completedFuture(newCustomer.id()))
                .get();
        Assert.assertNotNull(foundCustomer);
        Assert.assertEquals("foo1", foundCustomer.firstName().get());
        Assert.assertEquals("foo2", foundCustomer.lastName().get());
        Assert.assertEquals(Long.valueOf(12345L), foundCustomer.number().get());
    }

    @Test
    public void testFindAll() throws Exception {
        this.customers.add("foo1", "foo2", 12345L).get();
        this.customers.add("foo1", "foo2", 12345L).get();
        this.customers.add("foo1", "foo2", 12345L).get();
        this.customers.add("foo1", "foo2", 12345L).get();
        this.customers.add("foo1", "foo2", 12345L).get();

        Assert.assertEquals(5, this.customers.findAll().get().size());
    }

    @Test
    public void testRemoveAll() throws Exception {
        this.customers.removeAll().get();
        this.testFindAll();
        this.customers.removeAll().get();
        Assert.assertEquals(0, this.customers.findAll().get().size());
    }

    @Test
    public void testFindIdAndFirstNameByID() throws Exception {
        final Customer newCustomer = this.customers.add("foo1", "foo2", 12345L).get();
        final List<List<?>> result = this.customers.findIdAndFirstNameByID(
                CompletableFuture.completedFuture(newCustomer.id()), newCustomer.firstName()).get();
        Assert.assertEquals(1, result.size());
        final List<?> subList = result.get(0);
        Assert.assertEquals(2, subList.size());
        Assert.assertEquals(0L, subList.get(0));
        Assert.assertEquals("foo1", subList.get(1));
    }
}
