package co.ecso.jdao;

import co.ecso.jdao.database.query.DatabaseField;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;

/**
 * DatabaseFieldTest.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 18.08.16
 */
public final class DatabaseFieldTest {

    @Test
    public void name() {
        final DatabaseField<String> field = new DatabaseField<>("foo", String.class, Types.VARCHAR);
        Assert.assertEquals("foo", field.toString());
    }

    @Test
    public void defaultClass() {
        final DatabaseField<String> field = new DatabaseField<>("foo", String.class, Types.VARCHAR);
        Assert.assertEquals(String.class, field.valueClass());
    }
}
