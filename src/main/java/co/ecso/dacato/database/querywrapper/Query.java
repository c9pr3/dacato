package co.ecso.dacato.database.querywrapper;

import java.io.Serializable;

/**
 * Query.
 *
 * @param <T> Type of Query.
 * @author Christian Scharmach (cs@e-cs.co)
 * @since 19.09.16
 */
public interface Query<T> extends Serializable {

    /**
     * Get query.
     *
     * @return Query.
     */
    String query();

    /**
     * Get tableName.
     *
     * @return Table name.
     */
    String tableName();

    /**
     * Query type.
     *
     * @return Query type.
     */
    Class<T> queryType();
}
