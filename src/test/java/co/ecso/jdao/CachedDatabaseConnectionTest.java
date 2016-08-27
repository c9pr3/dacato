package co.ecso.jdao;

import co.ecso.jdao.hsql.HsqlConnection;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
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
    private static final CachingConnectionWrapper CONNECTION = new CachingConnectionWrapper(
            new HsqlConnection(APPLICATION_CONFIG), APPLICATION_CACHE);

    @Before
    public void setUp() throws Exception {
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
//        final CompletableFuture<Long> newInsertID = insertOne();
//        CONNECTION.findMany()
    }

    @Test
    public void findOneWithMap() throws Exception {
        final CompletableFuture<Long> newInsertID = insertOne();
        final DatabaseField<String> returnColumn = new DatabaseField<>("customer_first_name", "", Types.VARCHAR);
        final Map<DatabaseField, Object> columns = new LinkedHashMap<>();
        DatabaseField<Long> dbField = new DatabaseField<>("id", -1L, Types.BIGINT);
        columns.put(dbField, newInsertID.get());
        final Query query = new Query("SELECT %s FROM customer WHERE %s = ?");
        String res = (String)CONNECTION.findOne(query, columns, returnColumn).get();
        Assert.assertNotNull(res);
        Assert.assertEquals("foo", res);
    }

    @Test
    public void findOne() throws Exception {
        final CompletableFuture<Long> newInsertID = insertOne();
        final CompletableFuture<?> result = CONNECTION.findOne(new Query("SELECT %s FROM customer WHERE id = ?"),
                newInsertID, new DatabaseField<>("id", -1L, Types.BIGINT));
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

    @Test(expected = ExecutionException.class)
    public void removeAll() throws Exception {
        final CachingConnectionWrapper connection = new CachingConnectionWrapper(
                new HsqlConnection(APPLICATION_CONFIG), APPLICATION_CACHE);
        final Long newInsertID = connection.insert(new Query("INSERT INTO customer VALUES(NULL, ?, ?, ?, 'auth', " +
                "NULL, 'theme1', NULL)"), new LinkedHashMap<DatabaseField<?>, Object>() {{
            put(new DatabaseField<>("customer_first_name", null, Types.VARCHAR), "foo");
            put(new DatabaseField<>("customer_login_password", null, Types.VARCHAR), "password");
            put(new DatabaseField<>("customer_number", 2L, Types.BIGINT), 1234L);
        }}).get();
        CompletableFuture<?> fres = connection.findOne(new Query("SELECT id from customer where id = ?"), CompletableFuture.completedFuture(newInsertID),
                new DatabaseField<>("id", -1L, Types.VARCHAR));
        Assert.assertNotNull(fres);
        Long res = fres.handle((f, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            }
            if (f == null) {
                return null;
            }
            return Long.valueOf(f.toString().trim());
        }).get();
        Assert.assertEquals(newInsertID, res);

        connection.truncate(new Query("TRUNCATE table customer AND COMMIT")).get();

        fres = connection.findOne(new Query("SELECT id from customer where id = ?"), CompletableFuture.completedFuture(1L),
                new DatabaseField<>("id", -1L, Types.VARCHAR));
        Assert.assertNotNull(fres);
        fres.get();
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

}
