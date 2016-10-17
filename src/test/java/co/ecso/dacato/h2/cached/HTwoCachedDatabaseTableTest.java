package co.ecso.dacato.h2.cached;

import co.ecso.dacato.TestApplicationCache;
import co.ecso.dacato.database.cache.Cache;
import co.ecso.dacato.h2.AbstractHTwoTest;
import co.ecso.dacato.h2.HTwoTestApplicationConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * HTwoCachedDatabaseTableTest.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 06.09.16
 */
public final class HTwoCachedDatabaseTableTest extends AbstractHTwoTest {

    private HTwoCachedCustomers customers = null;

    @Before
    public void setUp() throws Exception {
        this.setUpHTwoDatabase();
        this.customers = new HTwoCachedCustomers(new HTwoTestApplicationConfig());
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupHTwoDatabase();
    }

    @Test
    public void testAdd() throws Exception {
        final HTwoCachedCustomer newCustomer = this.customers.create("foo1", 12345L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer);
        Assert.assertEquals("foo1", newCustomer.firstName().get().resultValue());
    }

    @Test
    public void testFindOne() throws Exception {
        final HTwoCachedCustomer newCustomer = this.customers.create("foo1", 12345L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer);

        final HTwoCachedCustomer foundCustomer1 = this.customers.findOne(newCustomer.primaryKey())
                .get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(foundCustomer1);
        Assert.assertEquals("foo1", foundCustomer1.firstName().get().resultValue());
        Assert.assertEquals(Long.valueOf(12345L), foundCustomer1.number().get().resultValue());

//        System.out.println("Getting by cache...");

        final HTwoCachedCustomer foundCustomer2 = this.customers.findOne(newCustomer.primaryKey())
                .get(10, TimeUnit.SECONDS);
        final HTwoCachedCustomer foundCustomer3 = this.customers.findOne(newCustomer.primaryKey())
                .get(10, TimeUnit.SECONDS);
        final HTwoCachedCustomer foundCustomer4 = this.customers.findOne(newCustomer.primaryKey())
                .get(10, TimeUnit.SECONDS);
        final HTwoCachedCustomer foundCustomer5 = this.customers.findOne(newCustomer.primaryKey())
                .get(10, TimeUnit.SECONDS);
        final HTwoCachedCustomer foundCustomer6 = this.customers.findOne(newCustomer.primaryKey())
                .get(10, TimeUnit.SECONDS);

        Assert.assertEquals(foundCustomer1.primaryKey(), foundCustomer2.primaryKey());
        Assert.assertEquals(foundCustomer2.primaryKey(), foundCustomer3.primaryKey());
        Assert.assertEquals(foundCustomer3.primaryKey(), foundCustomer4.primaryKey());
        Assert.assertEquals(foundCustomer4.primaryKey(), foundCustomer5.primaryKey());
        Assert.assertEquals(foundCustomer5.primaryKey(), foundCustomer6.primaryKey());
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void testCache() throws ExecutionException, InterruptedException, TimeoutException {
        final Cache<String, CompletableFuture<Long>> myCache = new TestApplicationCache<>();
        final Long longValue = myCache.get("foo", this::getLong).get(10, TimeUnit.SECONDS);
        final Long longValue2 = myCache.get("foo", this::getLong).get(10, TimeUnit.SECONDS);
        final Long longValue3 = myCache.get("foo", this::getLong).get(10, TimeUnit.SECONDS);
        final Long longValue4 = myCache.get("foo", this::getLong).get(10, TimeUnit.SECONDS);
        final Long longValue5 = myCache.get("foo", this::getLong).get(10, TimeUnit.SECONDS);

        Assert.assertEquals(longValue, longValue2);
        Assert.assertEquals(longValue, longValue3);
        Assert.assertEquals(longValue, longValue4);
        Assert.assertEquals(longValue, longValue5);
    }

    private CompletableFuture<Long> getLong() {
        CompletableFuture<Long> c = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
                c.complete(System.currentTimeMillis());
            } catch (final InterruptedException ignored) {
            }
        });
        return c;
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void testFindAll() throws Exception {
        Assert.assertEquals(Integer.valueOf(0), this.customers.findAll().thenApply(List::size).get());

        CompletableFuture.allOf(
                this.customers.create("foo1", 12345L),
                this.customers.create("foo1", 12345L),
                this.customers.create("foo1", 12345L),
                this.customers.create("foo1", 12345L),
                this.customers.create("foo1", 12345L)
        ).get(10, TimeUnit.SECONDS);

        Assert.assertEquals(5, this.customers.findAll().get().size());
    }

    @Test
    public void testRemoveAll() throws Exception {
        this.customers.removeAll().get(10, TimeUnit.SECONDS);
        this.testFindAll();
        this.customers.removeAll().get(10, TimeUnit.SECONDS);
        Assert.assertEquals(0, this.customers.findAll().get().size());
    }
}
