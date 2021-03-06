package co.ecso.dacato.hsql;

import org.junit.After;
import org.junit.Before;

/**
 * HSQLConnectionTest.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @version $Id:$
 * @since 28.08.16
 */
@SuppressWarnings("unused")
public final class HSQLConnectionTest extends AbstractHSQLTest {
    @Before
    public void setUp() throws Exception {
        this.setUpHSQLDatabase();
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupHSQLDatabase();
    }

}
