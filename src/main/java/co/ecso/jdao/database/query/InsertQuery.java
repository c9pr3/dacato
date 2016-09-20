package co.ecso.jdao.database.query;

import co.ecso.jdao.database.cache.CacheKey;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * InsertQuery.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 12.09.16
 */
@SuppressWarnings("WeakerAccess")
public final class InsertQuery<T> implements Query<T> {

    private final String query;
    private final Map<DatabaseField<?>, Object> values = new LinkedHashMap<>();
    private final DatabaseField<T> columnToReturn;

    public InsertQuery(final String query, final DatabaseField<T> columnToReturn) {
        this.query = query;
        this.columnToReturn = columnToReturn;
    }

    public <R> void add(final DatabaseField<R> field, final R value) {
        values.put(field, value);
    }

    @Override
    public String query() {
        return query;
    }

    @Override
    public CacheKey<T> getCacheKey() {
        return  new CacheKey<>(
                columnToReturn.valueClass(),
                query,
                columnToReturn.name(),
                columnToReturn.valueClass().getName(),
                columnToReturn.sqlType()
        );
    }

    public Map<DatabaseField<?>, ?> values() {
        return values;
    }

    public DatabaseField<T> columnToReturn() {
        return columnToReturn;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InsertQuery<?> that = (InsertQuery<?>) o;
        return Objects.equals(query, that.query) &&
                Objects.equals(values, that.values) &&
                Objects.equals(columnToReturn, that.columnToReturn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, values, columnToReturn);
    }
}
