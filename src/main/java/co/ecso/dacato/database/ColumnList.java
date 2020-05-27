package co.ecso.dacato.database;

import co.ecso.dacato.database.querywrapper.DatabaseField;

import java.util.Map;

/**
 * ColumnList.
 *
 * @author Christian Scharmach (cs@e-cs.co)
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
