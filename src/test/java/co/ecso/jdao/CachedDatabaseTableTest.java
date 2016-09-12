package co.ecso.jdao;

/**
 * CachedDatabaseTableTest.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 06.09.16
 */
public final class CachedDatabaseTableTest extends AbstractTest {

    /*
    private CachedCustomers customers = null;

    @Before
    public void setUp() throws Exception {
        this.setUpDatabase();
        this.customers = new CachedCustomers(APPLICATION_CONFIG);
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupDatabase();
    }

    @Test
    public void testAdd() throws Exception {
        final CachedCustomer newCustomer = this.customers.add("foo1", "foo2", 12345L).get();
        Assert.assertNotNull(newCustomer);
        Assert.assertEquals("foo1", newCustomer.firstName().get().value());
        Assert.assertEquals("foo2", newCustomer.lastName().get().value());
    }

    @Test
    public void testFindOne() throws Exception {
        final CachedCustomer newCustomer = this.customers.add("foo1", "foo2", 12345L).get();
        Assert.assertNotNull(newCustomer);

        final CachedCustomer foundCustomer = this.customers.findOne(newCustomer.id())
                .get();
        Assert.assertNotNull(foundCustomer);
        Assert.assertEquals("foo1", foundCustomer.firstName().get().value());
        Assert.assertEquals("foo2", foundCustomer.lastName().get().value());
        Assert.assertEquals(Long.valueOf(12345L), foundCustomer.number().get().value());

        final CachedCustomer foundCustomer2 = this.customers.findOne(newCustomer.id()).get();
        final CachedCustomer foundCustomer3 = this.customers.findOne(newCustomer.id()).get();
        final CachedCustomer foundCustomer4 = this.customers.findOne(newCustomer.id()).get();
        final CachedCustomer foundCustomer5 = this.customers.findOne(newCustomer.id()).get();
        final CachedCustomer foundCustomer6 = this.customers.findOne(newCustomer.id()).get();

        Assert.assertEquals(foundCustomer, foundCustomer2);
        Assert.assertEquals(foundCustomer2, foundCustomer3);
        Assert.assertEquals(foundCustomer3, foundCustomer4);
        Assert.assertEquals(foundCustomer4, foundCustomer5);
        Assert.assertEquals(foundCustomer5, foundCustomer6);
    }

    @Test
    public void testCache() throws ExecutionException {
        final Cache<String, CompletableFuture<Long>> myCache = new TestApplicationCache<>();
        final CompletableFuture<Long> longValue = myCache.get("foo", this::getLong);
        final CompletableFuture<Long> longValue2 = myCache.get("foo", this::getLong);
        final CompletableFuture<Long> longValue3 = myCache.get("foo", this::getLong);
        final CompletableFuture<Long> longValue4 = myCache.get("foo", this::getLong);
        final CompletableFuture<Long> longValue5 = myCache.get("foo", this::getLong);

        Assert.assertEquals(longValue, longValue2);
        Assert.assertEquals(longValue, longValue3);
        Assert.assertEquals(longValue, longValue4);
        Assert.assertEquals(longValue, longValue5);
    }

    private CompletableFuture<Long> getLong() {
        CompletableFuture<Long> c = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1000L);
                c.complete(System.currentTimeMillis());
            } catch (final InterruptedException ignored) {
            }
        });
        return c;
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void testFindAll() throws Exception {
        this.customers.add("foo1", "foo2", 12345L).get();
        this.customers.add("foo1", "foo2", 12345L).get();
        this.customers.add("foo1", "foo2", 12345L).get();
        this.customers.add("foo1", "foo2", 12345L).get();
        this.customers.add("foo1", "foo2", 12345L).get();

//        Assert.assertEquals(5, this.customers.findAll().get().size());
    }

    @Test
    public void testRemoveAll() throws Exception {
        this.customers.removeAll().get();
        this.testFindAll();
        this.customers.removeAll().get();
//        Assert.assertEquals(0, this.customers.findAll().get().size());
    }
    */
}
