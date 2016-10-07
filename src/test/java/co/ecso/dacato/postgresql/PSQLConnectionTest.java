package co.ecso.dacato.postgresql;

import co.ecso.dacato.AbstractTest;
import org.junit.After;
import org.junit.Before;

/**
 * PSQLConnectionTest
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 07.10.16
 */
@SuppressWarnings("unused")
public final class PSQLConnectionTest extends AbstractTest {
    @Before
    public void setUp() throws Exception {
        this.setUpPSQLDatabase();
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupPostgreSQLDatabase();
    }
}
