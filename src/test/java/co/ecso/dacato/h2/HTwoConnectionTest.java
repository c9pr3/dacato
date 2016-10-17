package co.ecso.dacato.h2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * HTwoConnectionTest
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 07.10.16
 */
@SuppressWarnings("unused")
public final class HTwoConnectionTest extends AbstractHTwoTest {
    @Before
    public void setUp() throws Exception {
        this.setUpHTwoDatabase();
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupHTwoDatabase();
    }

    @Test
    public void testConnection() throws Exception {

    }
}
