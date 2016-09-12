package co.ecso.jdao;

/**
 * CachedDatabaseEntityTest.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 06.09.16
 */
public final class CachedDatabaseEntityTest extends AbstractTest {

    /*
    private CachedCustomer customer;

    @Before
    public void setUp() throws Exception {
        this.setUpDatabase();
        this.customer = new CachedCustomers(APPLICATION_CONFIG).add("firstName", "lastName", 1234L).get();
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

    @SuppressWarnings("Duplicates")
    @Test
    public void testSave() throws Exception {
        final Long id = this.customer.id();
        this.customer.save(new SingleColumnUpdateQuery<>(
                "UPDATE customer SET %s = ?, %s = ? WHERE %s = ?",
                Customer.Fields.ID, this.customer.id())
                .add(Customer.Fields.FIRST_NAME, "foo1")
                .add(Customer.Fields.LAST_NAME, "bla1")
        ).get();

        this.customer = new CachedCustomers(APPLICATION_CONFIG).findOne(id).get();
        Assert.assertEquals("foo1", this.customer.firstName().get().value());
        Assert.assertEquals("bla1", this.customer.lastName().get().value());

        this.customer.save(new SingleColumnUpdateQuery<>(
                "UPDATE customer SET %s = ? WHERE %s = ?",
                Customer.Fields.ID, this.customer.id())
                .add(Customer.Fields.FIRST_NAME, "foo2")
        ).get();

        Assert.assertEquals("foo2", this.customer.firstName().get().value());
    }
    */
}
