package co.ecso.dacato.mysql;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * MySQLConnectionTest
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @version $Id:$
 * @since 07.10.16
 */
@SuppressWarnings("unused")
public final class MySQLConnectionTest extends AbstractMySQLTest {
    @Before
    public void setUp() throws Exception {
        this.setUpMySQLDatabase();
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupMySQLDatabase();
    }

    @Test
    public void testConnection() throws Exception {

    }
}
