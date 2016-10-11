package co.ecso.dacato.postgresql;

import org.junit.After;
import org.junit.Before;

/**
 * MySQLConnectionTest
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 07.10.16
 */
@SuppressWarnings("unused")
public final class PSQLConnectionTest extends AbstractPSQLTest {
    @Before
    public void setUp() throws Exception {
        this.setUpPSQLDatabase();
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupPostgreSQLDatabase();
    }
}
