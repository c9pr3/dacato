package co.ecso.dacato.cassandra;

import org.cassandraunit.CassandraUnit;
import org.cassandraunit.dataset.xml.AbstractXmlDataSet;
import org.junit.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * CassandraServiceTest.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 07.10.16
 */
public final class CassandraServiceTest extends AbstractCassandraTest {

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

    @Before
    public void setUp() throws Exception {
        this.setupCassandraDatabase();
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupCassandraDatabase();
    }

    @Test
    public void shouldHaveLoadAnExtendDataSet() throws Exception {
        Assert.assertNotNull(this.cassandraUnit.keyspace);
        Assert.assertEquals(this.cassandraUnit.keyspace.getKeyspaceName(), "otherKeyspaceName");
    }
}
