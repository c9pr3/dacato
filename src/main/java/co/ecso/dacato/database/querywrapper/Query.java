package co.ecso.dacato.database.querywrapper;

/**
 * Query.
 *
 * @param <T> Type of Query.
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 19.09.16
 */
public interface Query<T> {

    /**
     * Get query.
     *
     * @return Query.
     */
    String query();

    /**
     * Query type.
     *
     * @return Query type.
     */
    Class<T> queryType();
}
