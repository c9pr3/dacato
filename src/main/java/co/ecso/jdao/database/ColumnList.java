package co.ecso.jdao.database;

import java.util.Map;

/**
 * ColumnList.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 13.09.16
 */
@FunctionalInterface
public interface ColumnList {
    Map<DatabaseField<?>, Object> values();
}
