package co.ecso.jdao.hsql;

import co.ecso.jdao.AbstractTest;
import org.junit.After;
import org.junit.Before;

/**
 * HsqlConnectionTest.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 28.08.16
 */
public final class HsqlConnectionTest extends AbstractTest {
    @Before
    public void setUp() throws Exception {
        this.setUpDatabase();
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupDatabase();
    }
/*
    @Test
    public void testManyInserts() throws Exception {
        final Map<DatabaseField<?>, Object> map = new LinkedHashMap<>();
        map.put(Fields.FIRST_NAME, "firstName");
        map.put(Fields.LOGIN_PASSWORD, "loginPW");
        map.put(Fields.NUMBER, 1234L);
        map.put(Fields.AUTHORITY_ROLE, "USER");
        map.put(Fields.PRODUCT_OFFER_ID, -1L);
        map.put(Fields.THEME, "CERULEAN");
        map.put(Fields.SESSION, "");

        try {
            CompletableFuture.allOf(
                    ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert("INSERT INTO customer " +
                            "VALUES (null, ?, ?, ?, ?, ?, ?, ?)", map),
                    ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert("INSERT INTO customer " +
                            "VALUES (null, ?, ?, ?, ?, ?, ?, ?)", map),
                    ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert("INSERT INTO customer " +
                            "VALUES (null, ?, ?, ?, ?, ?, ?, ?)", map),
                    ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert("INSERT INTO customer " +
                            "VALUES (null, ?, ?, ?, ?, ?, ?, ?)", map),
                    ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert("INSERT INTO customer " +
                            "VALUES (null, ?, ?, ?, ?, ?, ?, ?)", map),
                    ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert("INSERT INTO customer " +
                            "VALUES (null, ?, ?, ?, ?, ?, ?, ?)", map),
                    ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert("INSERT INTO customer " +
                            "VALUES (null, ?, ?, ?, ?, ?, ?, ?)", map),
                    ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert("INSERT INTO customer " +
                            "VALUES (null, ?, ?, ?, ?, ?, ?, ?)", map),
                    ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert("INSERT INTO customer " +
                            "VALUES (null, ?, ?, ?, ?, ?, ?, ?)", map),
                    ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert("INSERT INTO customer " +
                            "VALUES (null, ?, ?, ?, ?, ?, ?, ?)", map),

                    ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert("INSERT INTO customer " +
                            "VALUES (null, ?, ?, ?, ?, ?, ?, ?)", map),
                    ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert("INSERT INTO customer " +
                            "VALUES (null, ?, ?, ?, ?, ?, ?, ?)", map),
                    ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert("INSERT INTO customer " +
                            "VALUES (null, ?, ?, ?, ?, ?, ?, ?)", map),
                    ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert("INSERT INTO customer " +
                            "VALUES (null, ?, ?, ?, ?, ?, ?, ?)", map),
                    ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert("INSERT INTO customer " +
                            "VALUES (null, ?, ?, ?, ?, ?, ?, ?)", map),
                    ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert("INSERT INTO customer " +
                            "VALUES (null, ?, ?, ?, ?, ?, ?, ?)", map),
                    ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert("INSERT INTO customer " +
                            "VALUES (null, ?, ?, ?, ?, ?, ?, ?)", map),
                    ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert("INSERT INTO customer " +
                            "VALUES (null, ?, ?, ?, ?, ?, ?, ?)", map),
                    ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert("INSERT INTO customer " +
                            "VALUES (null, ?, ?, ?, ?, ?, ?, ?)", map),
                    ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert("INSERT INTO customer " +
                            "VALUES (null, ?, ?, ?, ?, ?, ?, ?)", map)
            ).build();

            final MultipleFindQuery findQuery = new MultipleFindQuery(
                    "SELECT %s FROM customer",
                    new ColumnList().build(Fields.ID)
            );
            List res = ((MultipleColumnFinder) () -> APPLICATION_CONFIG).find(findQuery).build();

            Assert.assertEquals(20, res.size());

            final Map<DatabaseField<?>, CompletableFuture<?>> map1 = new ColumnList()
                    .build(new DatabaseField<>("id", -1L, Types.BIGINT), CompletableFuture.completedFuture(0L));
            final List<Long> res2 = ((SingleColumnFinder) () -> APPLICATION_CONFIG).find(
                    new ListFindQuery<>("SELECT %s FROM customer WHERE %s = ?", Fields.ID, map1)).build();
            Assert.assertEquals(1, res2.size());
        } catch (final InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTruncate() throws Exception {
//        testManyInserts();
//        final List<Long> res = ((SingleColumnFinder) () -> APPLICATION_CONFIG)
//                .find(new ListFindQuery<>("SELECT %s FROM customer", Fields.ID)).build();
//        Assert.assertEquals(20, res.size());
//        ((Truncater) () -> APPLICATION_CONFIG).truncate("TRUNCATE TABLE customer");
//
//        final List<Long> res2 = ((SingleColumnFinder) () -> APPLICATION_CONFIG)
//                .find(new ListFindQuery<>("SELECT %s FROM customer", Fields.ID)).build();
//        Assert.assertEquals(0, res2.size());
    }

    private static final class Fields {
        static final DatabaseField<Long> ID = new DatabaseField<>("id", -1L, Types.BIGINT);
        static final DatabaseField<String> THEME = new DatabaseField<>("customer_theme", "", Types.VARCHAR);
        static final DatabaseField<Long> PRODUCT_OFFER_ID =
                new DatabaseField<>("f_product_offer_id", -1L, Types.BIGINT);
        static final DatabaseField<String> AUTHORITY_ROLE =
                new DatabaseField<>("customer_authority_role", "", Types.VARCHAR);
        static final DatabaseField<Long> NUMBER = new DatabaseField<>("customer_number", -1L, Types.BIGINT);
        static final DatabaseField<String> LOGIN_PASSWORD =
                new DatabaseField<>("customer_login_password", "", Types.VARCHAR);
        static final DatabaseField<String> FIRST_NAME = new DatabaseField<>("customer_first_name", "", Types.VARCHAR);
        static final DatabaseField<String> SESSION = new DatabaseField<>("session", "", Types.VARCHAR);
    }
    */
}
