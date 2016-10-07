package co.ecso.dacato.hsql;

import co.ecso.dacato.AbstractTest;
import org.junit.After;
import org.junit.Before;

/**
 * HSQLConnectionTest.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 28.08.16
 */
@SuppressWarnings("unused")
public final class HSQLConnectionTest extends AbstractTest {
    @Before
    public void setUp() throws Exception {
        this.setUpHSQLDatabase();
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupHSQLDatabase();
    }

}
