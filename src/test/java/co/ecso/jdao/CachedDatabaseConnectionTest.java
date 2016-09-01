package co.ecso.jdao;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Types;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * CachedDatabaseConnectionTest.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 02.07.16
 */
public final class CachedDatabaseConnectionTest extends AbstractTest {
    private static CachingConnectionWrapper CONNECTION;

    @Before
    public void setUp() throws Exception {
        if (CONNECTION == null) {
            CONNECTION = new CachingConnectionWrapper(APPLICATION_CONFIG, APPLICATION_CACHE);
        }
        this.setUpDatabase();
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupDatabase();
    }

    @Test
    public void findMany() throws Exception {
        final Long newInsertID = insertOne().get();
        final Long newInsertID2 = insertOne().get();
        final LinkedHashMap<DatabaseField<?>, Object> map = new LinkedHashMap<>();
        map.put(new DatabaseField<>("customer_first_name", "", Types.VARCHAR), "foo");
        final List<?> result = CONNECTION.findMany(new Query("SELECT %s FROM customer WHERE %s = ?"),
                Fields.ID, map).get();
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());

        Assert.assertEquals(newInsertID, result.get(0));
        Assert.assertEquals(newInsertID2, result.get(1));
    }

    @Test
    public void findManyStrings() throws Exception {
        insertOne().get();
        insertOne().get();
        final LinkedHashMap<DatabaseField<?>, Object> map = new LinkedHashMap<>();
        map.put(new DatabaseField<>("customer_first_name", "", Types.VARCHAR), "foo");
        final List<?> result = CONNECTION.findMany(new Query("SELECT %s FROM customer WHERE %s = ?"),
                Fields.FIRST_NAME, map).get();
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        result.forEach(r -> Assert.assertEquals(r.getClass(), String.class));
    }

    @Test
    public void findOneWithMap() throws Exception {
        final CompletableFuture<Long> newInsertID = insertOne();
        final DatabaseField<String> returnColumn = new DatabaseField<>("customer_first_name", "", Types.VARCHAR);
        final LinkedHashMap<DatabaseField<?>, Object> columns = new LinkedHashMap<>();
        DatabaseField<?> dbField = new DatabaseField<>("id", -1L, Types.BIGINT);
        columns.put(dbField, newInsertID.get());
        final Query query = new Query("SELECT %s FROM customer WHERE %s = ?");
        CompletableFuture<String> res = ((Finder<String>) () -> APPLICATION_CONFIG)
                .findOne(query, returnColumn, columns);
        Assert.assertNotNull(res);
        Assert.assertEquals("foo", res.get());
    }

    @Test
    public void findOne() throws Exception {
        final CompletableFuture<Long> newInsertID = insertOne();
        final CompletableFuture<?> result = CONNECTION.findOne(new Query("SELECT %s FROM customer WHERE id = ?"),
                new DatabaseField<>("id", -1L, Types.BIGINT), newInsertID);
        Assert.assertNotNull(result);
        final Long res = result.handle((r, ex) -> {
            if (r == null) {
                return null;
            }
            if (ex != null) {
                ex.printStackTrace();
            }
            return Long.valueOf(r.toString().trim());
        })
                .get();
        Assert.assertNotNull(res);
        Assert.assertEquals(newInsertID.get(), res);
    }

    @Test
    public void testFindMultiple() throws Exception {
        CompletableFuture<Long> id = insertOne();
        CompletableFuture<String> firstName = ((Finder<String>) () -> APPLICATION_CONFIG)
                .findOne(new Query("SELECT %s FROM customer WHERE id = ?"),
                        new DatabaseField<>("customer_first_name", "", Types.VARCHAR), id);

        final Query query = new Query("SELECT %s, %s, %s FROM customer WHERE %s = ? AND %s = ?");

        final LinkedList<DatabaseField<?>> columnsToReturn = new LinkedList<>();
        columnsToReturn.add(new DatabaseField<>("id", -1L, Types.BIGINT));
        columnsToReturn.add(new DatabaseField<>("customer_first_name", "", Types.VARCHAR));
        columnsToReturn.add(new DatabaseField<>("customer_login_password", "", Types.VARCHAR));

        final LinkedHashMap<DatabaseField<?>, Object> columnsToSelect = new LinkedHashMap<>();
        columnsToSelect.put(new DatabaseField<>("id", -1L, Types.BIGINT), id.get());
        columnsToSelect.put(new DatabaseField<>("customer_first_name", "", Types.VARCHAR), firstName.get());

        final CompletableFuture<LinkedList<?>> rval = ((MultipleReturnFinder<Void>) () -> APPLICATION_CONFIG)
                .findeOne(query, columnsToReturn, columnsToSelect);

        final List<?> resList = rval.get(10, TimeUnit.SECONDS);

        Assert.assertEquals(3, resList.size());
        Assert.assertEquals("foo", resList.get(1));
        Assert.assertEquals("password", resList.get(2));
    }

    @Test
    public void update() throws Exception {
        final CachingConnectionWrapper connection = new CachingConnectionWrapper(APPLICATION_CONFIG, APPLICATION_CACHE);
        final CompletableFuture<Long> found = connection.findOne(new Query("SELECT %s FROM customer WHERE id = ?"),
                new DatabaseField<>("id", -1L, Types.BIGINT), insertOne()).handle((ok, ex) -> {
                    if (ex != null) {
                        ex.printStackTrace();
                    }
                    return Long.valueOf(ok.toString().trim());
        });
        Assert.assertNotNull(found);

        final String firstName = (String)connection.findOne(new Query("SELECT %s FROM customer WHERE id = ?"),
                new DatabaseField<>("customer_first_name", "", Types.VARCHAR), found).get();
        Assert.assertNotNull(firstName);
        Assert.assertEquals("foo", firstName);

        final LinkedHashMap<DatabaseField<?>, Object> map = new LinkedHashMap<>();
        map.put(new DatabaseField<>("customer_first_name", null, Types.VARCHAR), "foo1");
        final Boolean updated = connection.update(new Query("UPDATE customer SET %s = ? WHERE %s = ?"), map, found).get();
        Assert.assertTrue(updated);

        final String firstName1 = (String)connection.findOne(new Query("SELECT %s FROM customer WHERE id = ?"),
                new DatabaseField<>("customer_first_name", "", Types.VARCHAR), found).get();
        Assert.assertNotNull(firstName1);
        Assert.assertEquals("foo1", firstName1);
    }

    @Test
    public void removeAll() throws Exception {
        final CachingConnectionWrapper connection = new CachingConnectionWrapper(APPLICATION_CONFIG, APPLICATION_CACHE);

        connection.truncate(new Query("TRUNCATE TABLE customer")).get();

        final LinkedHashMap<DatabaseField<?>, Object> map = new LinkedHashMap<>();
        map.put(Fields.FIRST_NAME, "firstName");
        map.put(Fields.LOGIN_PASSWORD, "loginPW");
        map.put(Fields.NUMBER, 1234L);
        map.put(Fields.AUTHORITY_ROLE, "USER");
        map.put(Fields.PRODUCT_OFFER_ID, null);
        map.put(Fields.THEME, "CERULEAN");
        map.put(Fields.SESSION, "");

        CompletableFuture.allOf(
                CONNECTION.insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                CONNECTION.insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                CONNECTION.insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                CONNECTION.insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                CONNECTION.insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                CONNECTION.insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                CONNECTION.insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                CONNECTION.insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                CONNECTION.insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                CONNECTION.insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),

                CONNECTION.insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                CONNECTION.insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                CONNECTION.insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                CONNECTION.insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                CONNECTION.insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                CONNECTION.insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                CONNECTION.insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                CONNECTION.insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                CONNECTION.insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                CONNECTION.insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map)
        )
                .get();

        final List<?> fres = connection.findMany(new Query("SELECT %s from customer"),
                Fields.ID, new LinkedHashMap<>()).get();
        Assert.assertEquals(20, fres.size());
        connection.truncate(new Query("TRUNCATE table customer AND COMMIT")).get();
        final List<?> fres1 = connection.findMany(new Query("SELECT id from customer"),
                Fields.ID, new LinkedHashMap<>()).get();
        Assert.assertEquals(0, fres1.size());
    }

    private CompletableFuture<Long> insertOne() {
        return CONNECTION.insert(new Query("INSERT INTO customer VALUES(NULL, ?, ?, ?, 'auth', " +
                "NULL, 'theme1', NULL)"), new LinkedHashMap<DatabaseField<?>, Object>() {
                    {
                        put(new DatabaseField<>("customer_first_name", null, Types.VARCHAR), "foo");
                        put(new DatabaseField<>("customer_login_password", null, Types.VARCHAR), "password");
                        put(new DatabaseField<>("customer_number", 2L, Types.BIGINT), 1234L);
                    }
                }
        );
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
//        static final DatabaseField<String> NAME = new DatabaseField<>("offer_name", "", Types.VARCHAR);
//        static final DatabaseField<Float> PRICE = new DatabaseField<>("offer_price", 0.0F, Types.FLOAT);
//        static final DatabaseField<Boolean> DISPLAYABLE = new DatabaseField<>("displayable", false, Types.BOOLEAN);
    }
}
