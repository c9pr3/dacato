package co.ecso.jdao;

import co.ecso.jdao.database.DatabaseField;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;

/**
 * DatabaseFieldTest.
 *
 * @since 18.08.16
 * @version $Id:$
 * @author Christian Senkowski (cs@2scale.net)
 */
public final class DatabaseFieldTest {

    @Test
    public void name() {
        final DatabaseField<String> field = new DatabaseField<>("foo", "123", Types.VARCHAR);
        Assert.assertEquals("foo", field.toString());
    }

    @Test
    public void defaultClass() {
        final DatabaseField<String> field = new DatabaseField<>("foo", "123", Types.VARCHAR);
        Assert.assertEquals(String.class, field.valueClass());
    }
}
