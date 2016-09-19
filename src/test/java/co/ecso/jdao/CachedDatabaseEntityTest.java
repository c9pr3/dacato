package co.ecso.jdao;

import co.ecso.jdao.helpers.CachedCustomer;
import co.ecso.jdao.helpers.CachedCustomers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * CachedDatabaseEntityTest.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 06.09.16
 */
public final class CachedDatabaseEntityTest extends AbstractTest {

    private CachedCustomer customer;

    @Before
    public void setUp() throws Exception {
        this.setUpDatabase();
        this.customer = new CachedCustomers(APPLICATION_CONFIG).create(
                "firstName", "lastName", 1234L
        ).get();
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
        Assert.assertEquals("lastName", this.customer.lastName().get().value());
        Assert.assertEquals("lastName", this.customer.lastName().get().value());
    }

    @Test
    public void testNumber() throws Exception {
        Assert.assertEquals(Long.valueOf(1234L), this.customer.number().get().value());
    }

}
