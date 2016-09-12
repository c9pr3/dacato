package co.ecso.jdao.database;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SingleColumnUpdateQuery.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 11.09.16
 */
@SuppressWarnings("WeakerAccess")
public final class SingleColumnUpdateQuery<T> {

    private final String query;
    private final DatabaseField<T> whereColumn;
    private final T whereValue;
    private final Map<DatabaseField<?>, Object> values = new LinkedHashMap<>();

    public SingleColumnUpdateQuery(final String query, final DatabaseField<T> whereColumn, final T whereValue) {
        this.query = query;
        this.whereColumn = whereColumn;
        this.whereValue = whereValue;
    }

    public <R> SingleColumnUpdateQuery<T> add(final DatabaseField<R> field, final R value) {
        values.put(field, value);
        return this;
    }

    public String query() {
        return query;
    }

    public DatabaseField<T> whereColumn() {
        return whereColumn;
    }

    public T whereValue() {
        return whereValue;
    }

    public Map<DatabaseField<?>, ?> values() {
        return values;
    }
}
