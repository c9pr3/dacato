package co.ecso.jdao;

/**
 * CLASS
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 26.08.16
 */
@SuppressWarnings("WeakerAccess")
public class Query {

    private final String query;

    public Query(String s) {
        this.query = s;
    }

    @Override
    public String toString() {
        return query;
    }

    public String getQuery() {
        return query;
    }
}
