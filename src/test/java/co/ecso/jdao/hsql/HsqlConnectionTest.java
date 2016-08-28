package co.ecso.jdao.hsql;

import co.ecso.jdao.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Types;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

    @Test
    public void testTruncate() throws Exception {
        testManyInserts();
        LinkedList<Long> res = ((Finder<Long>) () -> APPLICATION_CONFIG)
                .findMany(new Query("SELECT %s FROM customer"), Fields.ID, new HashMap<>()).get();
        Assert.assertEquals(20, res.size());
        ((Truncater) () -> APPLICATION_CONFIG).truncate(new Query("TRUNCATE TABLE customer"));
        LinkedList<Long> res2 = ((Finder<Long>) () -> APPLICATION_CONFIG)
                .findMany(new Query("SELECT %s FROM customer"), Fields.ID, new HashMap<>()).get();
        Assert.assertEquals(0, res2.size());
    }

    @Test
    public void testManyInserts() throws Exception {
        final Map<DatabaseField<?>, Object> map1 = new LinkedHashMap<>();
        map1.put(Fields.NAME, "offername1");
        map1.put(Fields.PRICE, 1.0F);
        map1.put(Fields.DISPLAYABLE, true);

        CompletableFuture<Long> productOffer = ((Inserter<Long>) () -> APPLICATION_CONFIG)
                .insert(new Query("INSERT INTO product_offer VALUES (null, ?, ?, ?)"), map1);

        productOffer.thenAccept(offerId1 -> {
            final Map<DatabaseField<?>, Object> map = new LinkedHashMap<>();
            map.put(Fields.FIRST_NAME, "firstName");
            map.put(Fields.LOGIN_PASSWORD, "loginPW");
            map.put(Fields.NUMBER, 1234L);
            map.put(Fields.AUTHORITY_ROLE, "USER");
            map.put(Fields.PRODUCT_OFFER_ID, offerId1);
            map.put(Fields.THEME, "CERULEAN");
            map.put(Fields.SESSION, "");

            try {
                CompletableFuture.allOf(
                        ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                        ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                        ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                        ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                        ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                        ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                        ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                        ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                        ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                        ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),

                        ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                        ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                        ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                        ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                        ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                        ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                        ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                        ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                        ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map),
                        ((Inserter<CompletableFuture<Long>>) () -> APPLICATION_CONFIG).insert(new Query("INSERT INTO customer VALUES (null, ?, ?, ?, ?, ?, ?, ?)"), map)
                ).get();

                LinkedList<Long> res = ((Finder<Long>) () -> APPLICATION_CONFIG)
                        .findMany(new Query("SELECT %s FROM customer"), Fields.ID, new HashMap<>()).get();
                Assert.assertEquals(20, res.size());
            } catch (final InterruptedException | ExecutionException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }).get();
    }

    private static final class Fields {
        static final DatabaseField<Long> ID = new DatabaseField<>("id", -1L, Types.BIGINT);
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