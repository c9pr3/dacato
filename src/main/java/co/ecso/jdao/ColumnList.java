package co.ecso.jdao;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * ColumnList.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 02.09.16
 */
public final class ColumnList {
    public List<DatabaseField<?>> get(final DatabaseField<?> ...fields) {
        return Collections.synchronizedList(Collections.unmodifiableList(Arrays.asList(fields)));
    }
}
