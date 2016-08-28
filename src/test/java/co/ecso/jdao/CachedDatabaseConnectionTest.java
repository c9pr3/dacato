package co.ecso.jdao;

import co.ecso.jdao.hsql.HsqlConnection;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Types;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
            CONNECTION = new CachingConnectionWrapper(
                    new HsqlConnection(APPLICATION_CONFIG), APPLICATION_CACHE);
        }
        this.setUpDatabase();
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupDatabase();
    }

    @Test
    public void testGetConnection() throws Exception {
        final CachingConnectionWrapper cachedDBConnection = new CachingConnectionWrapper(
                new HsqlConnection(APPLICATION_CONFIG), APPLICATION_CACHE);
        final Connection connection = cachedDBConnection.pooledConnection();
        Assert.assertNotNull(connection);
    }

    @Test
    public void findMany() throws Exception {
        final Long newInsertID = insertOne().get();
        final Long newInsertID2 = insertOne().get();
        final Map<DatabaseField<?>, Object> map = new LinkedHashMap<>();
        map.put(new DatabaseField<>("customer_first_name", "", Types.VARCHAR), "foo");
        final LinkedList<?> result = CONNECTION.findMany(new Query("SELECT * FROM customer WHERE %s = ?"), map).get();
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(newInsertID, result.get(0));
        Assert.assertEquals(newInsertID2, result.get(1));
    }

    @Test
    public void findOneWithMap() throws Exception {
        final CompletableFuture<Long> newInsertID = insertOne();
        final DatabaseField<String> returnColumn = new DatabaseField<>("customer_first_name", "", Types.VARCHAR);
        final Map<DatabaseField<?>, Object> columns = new LinkedHashMap<>();
        DatabaseField<?> dbField = new DatabaseField<>("id", -1L, Types.BIGINT);
        columns.put(dbField, newInsertID.get());
        final Query query = new Query("SELECT %s FROM customer WHERE %s = ?");
        CompletableFuture<String> res = ((Finder<CompletableFuture<String>, String>) () -> APPLICATION_CONFIG)
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
        }).get();
        Assert.assertNotNull(res);
        Assert.assertEquals(newInsertID.get(), res);
    }

    @Test
    public void removeAll() throws Exception {
        final CachingConnectionWrapper connection = new CachingConnectionWrapper(
                new HsqlConnection(APPLICATION_CONFIG), APPLICATION_CACHE);

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
        ).get();

        final LinkedList<?> fres = connection.findMany(new Query("SELECT id from customer"),
                new HashMap<>()).get();
        Assert.assertEquals(20, fres.size());
        connection.truncate(new Query("TRUNCATE table customer AND COMMIT")).get();
        final LinkedList<?> fres1 = connection.findMany(new Query("SELECT id from customer"),
                new HashMap<>()).get();
        Assert.assertEquals(0, fres1.size());
    }

    @Test
    public void selectIdWithValues() throws Exception {
        final CachingConnectionWrapper connection = new CachingConnectionWrapper(
                new HsqlConnection(APPLICATION_CONFIG), APPLICATION_CACHE);
        Assert.assertNotNull(connection);

    }

    @Test
    public void selectString() throws Exception {
        final CachingConnectionWrapper connection = new CachingConnectionWrapper(
                new HsqlConnection(APPLICATION_CONFIG), APPLICATION_CACHE);
        Assert.assertNotNull(connection);

    }

    private CompletableFuture<Long> insertOne() {
        return CONNECTION.insert(new Query("INSERT INTO customer VALUES(NULL, ?, ?, ?, 'auth', " +
                "NULL, 'theme1', NULL)"), new LinkedHashMap<DatabaseField<?>, Object>() {{
            put(new DatabaseField<>("customer_first_name", null, Types.VARCHAR), "foo");
            put(new DatabaseField<>("customer_login_password", null, Types.VARCHAR), "password");
            put(new DatabaseField<>("customer_number", 2L, Types.BIGINT), 1234L);
        }});
    }

    private static final class Fields {
        static final DatabaseField<String> THEME = new DatabaseField<>("customer_theme", "", Types.VARCHAR);
        static final DatabaseField<Long> PRODUCT_OFFER_ID = new DatabaseField<>("f_product_offer_id", -1L, Types.BIGINT);
        static final DatabaseField<String> AUTHORITY_ROLE = new DatabaseField<>("customer_authority_role", "", Types.VARCHAR);
        static final DatabaseField<Long> NUMBER = new DatabaseField<>("customer_number", -1L, Types.BIGINT);
        static final DatabaseField<String> LOGIN_PASSWORD = new DatabaseField<>("customer_login_password", "", Types.VARCHAR);
        static final DatabaseField<String> FIRST_NAME = new DatabaseField<>("customer_first_name", "", Types.VARCHAR);
        static final DatabaseField<String> SESSION = new DatabaseField<>("session", "", Types.VARCHAR);
        static final DatabaseField<String> NAME = new DatabaseField<>("offer_name", "", Types.VARCHAR);
        static final DatabaseField<Float> PRICE = new DatabaseField<>("offer_price", 0.0F, Types.FLOAT);
        static final DatabaseField<Boolean> DISPLAYABLE = new DatabaseField<>("displayable", false, Types.BOOLEAN);
    }
}
