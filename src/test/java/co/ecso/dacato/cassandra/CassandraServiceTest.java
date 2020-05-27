package co.ecso.dacato.cassandra;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * CassandraServiceTest.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @version $Id:$
 * @since 07.10.16
 */
public final class CassandraServiceTest extends AbstractCassandraTest {

/*
    @Rule
    public final CassandraUnit cassandraUnit = new CassandraUnit(
            new AbstractXmlDataSet(SRC_TEST_CONFIG_EXTENDED_DATA_SET_XML) {
                @Override
                protected InputStream getInputDataSetLocation(final String s) {
                    try {
                        return new FileInputStream(SRC_TEST_CONFIG_EXTENDED_DATA_SET_XML);
                    } catch (final FileNotFoundException e) {
                        return null;
                    }
                }
            });
*/

    @Before
    public void setUp() throws Exception {
        this.setupCassandraDatabase();
    }

    @After
    public void tearDown() {
        this.cleanupCassandraDatabase();
    }

    @Test
    public void shouldHaveLoadAnExtendDataSet() {
//        Assert.assertNotNull(this.cassandraUnit.keyspace);
//        Assert.assertEquals(this.cassandraUnit.keyspace.getKeyspaceName(), "otherKeyspaceName");
    }
}
