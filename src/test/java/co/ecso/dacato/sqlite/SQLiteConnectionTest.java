package co.ecso.dacato.sqlite;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;

/**
 * SQLiteConnectionTest
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @version $Id:$
 * @since 07.10.16
 */
@SuppressWarnings("unused")
public final class SQLiteConnectionTest extends AbstractSQLiteTest {
    @Before
    public void setUp() throws Exception {
        this.setUpSQLiteDatabase();
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupMySQLiteDatabase();
    }

    @Test
    public void testConnection() throws Exception {
        try (final Connection c = new SQLiteTestApplicationConfig().databaseConnectionPool().getConnection()) {
            Assert.assertNotNull(c);
        }
    }
}
