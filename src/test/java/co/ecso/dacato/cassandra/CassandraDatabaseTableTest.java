package co.ecso.dacato.cassandra;

import co.ecso.dacato.database.querywrapper.DatabaseField;
import co.ecso.dacato.database.querywrapper.DatabaseResultField;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * PSQLDatabaseTableTest.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @version $Id:$
 * @since 03.09.16
 */
@Ignore
public final class CassandraDatabaseTableTest extends AbstractCassandraTest {

  /*  @Rule
    public final CassandraUnit cassandraUnit = new CassandraUnit(
            new AbstractCQLDataSet(SRC_TEST_CONFIG_EXTENDED_DATA_SET_XML) {
                @Override
                protected InputStream getInputDataSetLocation(final String s) {
                    try {
                        return new FileInputStream(SRC_TEST_CONFIG_EXTENDED_DATA_SET_XML);
                    } catch (final FileNotFoundException e) {
                        return null;
                    }
                }
            });*/

    private CassandraCustomers customers = null;

    @Before
    public void setUp() throws Exception {
        this.setupCassandraDatabase();
        this.customers = new CassandraCustomers(new CassandraTestApplicationConfig());
    }

    @After
    public void tearDown() {
        this.cleanupCassandraDatabase();
    }

    @Test
    public void testAdd() throws Exception {
        final CassandraCustomer newCustomer = this.customers.create("foo1", 12345L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer);
        final CompletableFuture<DatabaseResultField<String>> firstNameFuture = newCustomer.firstName();
        Assert.assertNotNull(firstNameFuture);
        final DatabaseResultField<String> firstNameFutureResult = firstNameFuture.get();
        Assert.assertNotNull(firstNameFutureResult);
        Assert.assertEquals("foo1", firstNameFutureResult.resultValue());
        final CassandraCustomer newCustomer2 = this.customers.create("foo2", 12345L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer2);
        Assert.assertEquals("foo2", newCustomer2.firstName().get().resultValue());
        Assert.assertNotEquals(newCustomer.primaryKey(), newCustomer2.primaryKey());
    }

    @Test
    public void testFindOne() throws Exception {
        final CassandraCustomer newCustomer = this.customers.create("foo1", 12345L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer);

        final CassandraCustomer foundCustomer = this.customers.findOne(newCustomer.primaryKey())
                .get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(foundCustomer);
        Assert.assertEquals("foo1", foundCustomer.firstName().get(10, TimeUnit.SECONDS).resultValue());
        Assert.assertEquals(Long.valueOf(12345L), foundCustomer.number().get(10, TimeUnit.SECONDS).resultValue());
    }

 /*   @Test
    public void testFindOneByFirstName() throws Exception {
        final CassandraCustomer newCustomer = this.customers.create("foo1", 12345L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer);

        final CassandraCustomer foundCustomer = this.customers.findOneByFirstName(newCustomer.firstName()
                .get(10, TimeUnit.SECONDS)
                .resultValue()).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(foundCustomer);
        Assert.assertEquals("foo1", foundCustomer.firstName().get(10, TimeUnit.SECONDS).resultValue());
        Assert.assertEquals(Long.valueOf(12345L), foundCustomer.number().get(10, TimeUnit.SECONDS).resultValue());
    }*/

    @Test
    public void testFindFirstNameAndLastNameById() throws Exception {
        final CassandraCustomer newCustomer = this.customers.create("foo1", 12345L).get(10, TimeUnit.SECONDS);
        final CassandraCustomer newCustomer2 = this.customers.create("foo1", 1235L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer);
        Assert.assertNotNull(newCustomer2);

        final Map<DatabaseField<?>, DatabaseResultField<?>> firstName = this.customers.
                findFirstNameById(newCustomer.primaryKey()).get(10, TimeUnit.SECONDS);
        Assert.assertEquals(1, firstName.size());
        Assert.assertEquals("foo1", firstName.get(CassandraCustomer.Fields.FIRST_NAME).resultValue());
    }

    @Test
    public void testFindManyFirstNameAndLastNameById() throws Exception {
        final CassandraCustomer newCustomer = this.customers.create("foo1", 12345L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer);
        final CassandraCustomer newCustomer2 = this.customers.create("foo2", 12346L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer2);

        final List<CassandraCustomer> all = this.customers.findAll().get(10, TimeUnit.SECONDS);
        Assert.assertEquals(2, all.size());

        final List<Map<DatabaseField<?>, DatabaseResultField<?>>> firstNameAndLastName = this.customers.
                findManyFirstName().get(10, TimeUnit.SECONDS);

        Assert.assertEquals(2, firstNameAndLastName.size());

        Assert.assertEquals("foo1", firstNameAndLastName.get(0).get(CassandraCustomer.Fields.FIRST_NAME).resultValue());
        Assert.assertEquals("foo2", firstNameAndLastName.get(1).get(CassandraCustomer.Fields.FIRST_NAME).resultValue());
    }

    @Test
    public void testRemoveOne() throws Exception {
        final CassandraCustomer newCustomer = this.customers.create("foo1", 12345L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer);
        final CassandraCustomer newCustomer2 = this.customers.create("foo2", 12346L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer2);

        final List<CassandraCustomer> all = this.customers.findAll().get(10, TimeUnit.SECONDS);
        Assert.assertEquals(2, all.size());

        final Integer count = this.customers.removeOne(newCustomer.primaryKey()).get(10, TimeUnit.SECONDS);
        Assert.assertEquals(Integer.valueOf(1), count);

        final List<CassandraCustomer> all2 = this.customers.findAll().get(10, TimeUnit.SECONDS);
        Assert.assertEquals(1, all2.size());
    }

   /* @Test
    public void testFindAllByFirstName() throws Exception {
        CompletableFuture.allOf(
                this.customers.create("foo1", 12345L),
                this.customers.create("foo1", 12345L),
                this.customers.create("foo1", 12345L),
                this.customers.create("foo2", 12345L),
                this.customers.create("foo2", 12345L)
        ).get(10, TimeUnit.SECONDS);

        final List<CassandraCustomer> foundCustomer = this.customers.findAllByFirstName("foo1")
        .get(5, TimeUnit.SECONDS);

        Assert.assertNotNull(foundCustomer);
        Assert.assertEquals(3, foundCustomer.size());
    }*/

  /*  @SuppressWarnings("Duplicates")
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
    }*/

   /* @Test
    public void testFindOneByFirstNameAndLastName() throws Exception {
        CompletableFuture.allOf(
                this.customers.create("foo1", 12345L),
                this.customers.create("foo1", 12345L),
                this.customers.create("foo1", 12345L),
                this.customers.create("foo2", 12345L),
                this.customers.create("foo2", 12345L)
        ).get(10, TimeUnit.SECONDS);

        final CassandraCustomer found = this.customers.findOneByFirstName("foo1").get(5, TimeUnit.SECONDS);
        Assert.assertNotNull(found);
        Assert.assertEquals("foo1", found.firstName().get(10, TimeUnit.SECONDS).resultValue());
    }*/

    @Test
    public void testRemoveAll() throws Exception {
        this.customers.removeAll().get(10, TimeUnit.SECONDS);
//        this.testFindAll();
        this.customers.removeAll().get(10, TimeUnit.SECONDS);
        Assert.assertEquals(0, this.customers.findAll().get(10, TimeUnit.SECONDS).size());
    }

}
