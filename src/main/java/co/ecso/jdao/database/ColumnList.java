package co.ecso.jdao.database;

import co.ecso.jdao.database.query.DatabaseField;

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
    /**
     * Get column list columnValuesToSet.
     *
     * @return Values.
     */
    Map<DatabaseField<?>, Object> values();
}
