package co.ecso.jdao.database;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * InsertQuery.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 12.09.16
 */
@SuppressWarnings("WeakerAccess")
public final class InsertQuery<T> {

    private final String query;
    private final Map<DatabaseField<?>, Object> values = new LinkedHashMap<>();
    private final DatabaseField<T> columnToReturn;

    public InsertQuery(final String query, final DatabaseField<T> columnToReturn) {
        this.query = query;
        this.columnToReturn = columnToReturn;
    }

    public <R> InsertQuery add(final DatabaseField<R> field, final R value) {
        values.put(field, value);
        return this;
    }

    public String query() {
        return query;
    }

    public Map<DatabaseField<?>, ?> values() {
        return values;
    }

    public DatabaseField<T> columnToReturn() {
        return columnToReturn;
    }
}
