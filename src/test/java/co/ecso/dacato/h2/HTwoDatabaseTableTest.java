package co.ecso.dacato.h2;

import co.ecso.dacato.database.querywrapper.DatabaseField;
import co.ecso.dacato.database.querywrapper.DatabaseResultField;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * HTwoDatabaseTableTest.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 03.09.16
 */
public final class HTwoDatabaseTableTest extends AbstractHTwoTest {

    private HTwoCustomers customers = null;

    @Before
    public void setUp() throws Exception {
        this.setUpHTwoDatabase();
        this.customers = new HTwoCustomers(new HTwoTestApplicationConfig());
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupHTwoDatabase();
    }

    @Test
    public void testAdd() throws Exception {
        final HTwoCustomer newCustomer = this.customers.create("foo1", 12345L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer);
        Assert.assertEquals("foo1", newCustomer.firstName().get().resultValue());
        final HTwoCustomer newCustomer2 = this.customers.create("foo2", 12345L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer2);
        Assert.assertEquals("foo2", newCustomer2.firstName().get().resultValue());
        Assert.assertNotEquals(newCustomer.primaryKey(), newCustomer2.primaryKey());
    }

    @Test
    public void testFindOne() throws Exception {
        final HTwoCustomer newCustomer = this.customers.create("foo1", 12345L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer);

        final HTwoCustomer foundCustomer = this.customers.findOne(newCustomer.primaryKey()).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(foundCustomer);
        Assert.assertEquals("foo1", foundCustomer.firstName().get(10, TimeUnit.SECONDS).resultValue());
        Assert.assertEquals(Long.valueOf(12345L), foundCustomer.number().get(10, TimeUnit.SECONDS).resultValue());
    }

    @Test
    public void testFindOneByFirstName() throws Exception {
        final HTwoCustomer newCustomer = this.customers.create("foo1", 12345L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer);
        final HTwoCustomer newCustomer2 = this.customers.create("foo2", 12345L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer2);

        final String firstName = newCustomer.firstName().get(10, TimeUnit.SECONDS).resultValue();
        Assert.assertNotNull(firstName);
        final HTwoCustomer foundCustomer = this.customers.findOneByFirstName(firstName).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(foundCustomer);
        Assert.assertEquals("foo1", foundCustomer.firstName().get(10, TimeUnit.SECONDS).resultValue());
        Assert.assertEquals(Long.valueOf(12345L), foundCustomer.number().get(10, TimeUnit.SECONDS).resultValue());
    }

    @Test
    public void testFindFirstNameAndLastNameById() throws Exception {
        final HTwoCustomer newCustomer = this.customers.create("foo1", 12345L).get(10, TimeUnit.SECONDS);
        final HTwoCustomer newCustomer2 = this.customers.create("foo1", 1235L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer);
        Assert.assertNotNull(newCustomer2);

        final Map<DatabaseField, DatabaseResultField> firstName = this.customers.
                findFirstNameById(newCustomer.primaryKey()).get(10, TimeUnit.SECONDS);
        Assert.assertEquals(1, firstName.size());
        Assert.assertEquals("foo1", firstName.get(HTwoCustomer.Fields.FIRST_NAME).resultValue());
    }

    @Test
    public void testFindManyFirstNameAndLastNameById() throws Exception {
        final HTwoCustomer newCustomer = this.customers.create("foo1", 12345L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer);
        final HTwoCustomer newCustomer2 = this.customers.create("foo2", 12346L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer2);

        final List<HTwoCustomer> all = this.customers.findAll().get(10, TimeUnit.SECONDS);
        Assert.assertEquals(2, all.size());

        final List<Map<DatabaseField, DatabaseResultField>> firstNameAndLastName = this.customers.
                findManyFirstName().get(10, TimeUnit.SECONDS);

        Assert.assertEquals(2, firstNameAndLastName.size());

        Assert.assertEquals("foo1", firstNameAndLastName.get(0).get(HTwoCustomer.Fields.FIRST_NAME).resultValue());
        Assert.assertEquals("foo2", firstNameAndLastName.get(1).get(HTwoCustomer.Fields.FIRST_NAME).resultValue());
    }

    @Test
    public void testRemoveOne() throws Exception {
        final HTwoCustomer newCustomer = this.customers.create("foo1", 12345L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer);
        final HTwoCustomer newCustomer2 = this.customers.create("foo2", 12346L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer2);

        final List<HTwoCustomer> all = this.customers.findAll().get(10, TimeUnit.SECONDS);
        Assert.assertEquals(2, all.size());

        final Integer count = this.customers.removeOne(newCustomer.primaryKey()).get(10, TimeUnit.SECONDS);
        Assert.assertEquals(Integer.valueOf(1), count);

        final List<HTwoCustomer> all2 = this.customers.findAll().get(10, TimeUnit.SECONDS);
        Assert.assertEquals(1, all2.size());
    }

    @Test
    public void testFindAllByFirstName() throws Exception {
        CompletableFuture.allOf(
                this.customers.create("foo1", 12345L),
                this.customers.create("foo1", 12345L),
                this.customers.create("foo1", 12345L),
                this.customers.create("foo2", 12345L),
                this.customers.create("foo2", 12345L)
        ).get(10, TimeUnit.SECONDS);

        final List<HTwoCustomer> foundCustomer = this.customers.findAllByFirstName("foo1").get(5, TimeUnit.SECONDS);

        Assert.assertNotNull(foundCustomer);
        Assert.assertEquals(3, foundCustomer.size());
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void testFindAll() throws Exception {
        CompletableFuture.allOf(
                this.customers.create("foo1", 12345L),
                this.customers.create("foo1", 12345L),
                this.customers.create("foo1", 12345L),
                this.customers.create("foo1", 12345L),
                this.customers.create("foo1", 12345L)
        ).get(10, TimeUnit.SECONDS);

        Assert.assertEquals(5, this.customers.findAll().get(5, TimeUnit.SECONDS).size());
    }

    @Test
    public void testFindOneByFirstNameAndLastName() throws Exception {
        CompletableFuture.allOf(
                this.customers.create("foo1", 12345L),
                this.customers.create("foo1", 12345L),
                this.customers.create("foo1", 12345L),
                this.customers.create("foo2", 12345L),
                this.customers.create("foo2", 12345L)
        ).get(10, TimeUnit.SECONDS);

        final HTwoCustomer found = this.customers.findOneByFirstName("foo1").get(5, TimeUnit.SECONDS);
        Assert.assertNotNull(found);
        Assert.assertEquals("foo1", found.firstName().get(10, TimeUnit.SECONDS).resultValue());
    }

    @Test
    public void testRemoveAll() throws Exception {
        this.customers.removeAll().get(10, TimeUnit.SECONDS);
        this.testFindAll();
        this.customers.removeAll().get(10, TimeUnit.SECONDS);
        Assert.assertEquals(0, this.customers.findAll().get(10, TimeUnit.SECONDS).size());
    }

}
