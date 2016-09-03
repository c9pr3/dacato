package co.ecso.jdao;

import co.ecso.jdao.database.ColumnList;
import co.ecso.jdao.helpers.Customer;
import co.ecso.jdao.helpers.Customers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

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
        this.customer = new Customers(APPLICATION_CONFIG).add("firstName", "lastName", 1234L).get();
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
        Assert.assertEquals("firstName", this.customer.firstName().get());
    }

    @Test
    public void testLastName() throws Exception {
        Assert.assertEquals("lastName", this.customer.lastName().get());
    }

    @Test
    public void testNumber() throws Exception {
        Assert.assertEquals(Long.valueOf(1234L), this.customer.number().get());
    }

    @Test
    public void testSave() throws Exception {
        this.customer = this.customer.save(
                new ColumnList().keys(
                        Customer.Fields.FIRST_NAME, Customer.Fields.LAST_NAME, Customer.Fields.NUMBER
                ).values(Arrays.asList("foo1", "bla1", customer.number().get())).get(),
                new ColumnList().keys(Customer.Fields.ID).values(Collections.singletonList(this.customer.id())).get()
        ).get();

        Assert.assertEquals("foo1", this.customer.firstName().get());
        Assert.assertEquals("bla1", this.customer.lastName().get());

        this.customer = this.customer.save(
                new ColumnList().keys(
                        Customer.Fields.FIRST_NAME, Customer.Fields.LAST_NAME, Customer.Fields.NUMBER
                ).values(Arrays.asList("foo2", customer.lastName().get(), customer.number().get())).get(),
                new ColumnList().keys(Customer.Fields.ID).values(Collections.singletonList(this.customer.id())).get()
        ).get();

        Assert.assertEquals("foo2", this.customer.firstName().get());
        Assert.assertEquals("bla1", this.customer.lastName().get());
    }
}
