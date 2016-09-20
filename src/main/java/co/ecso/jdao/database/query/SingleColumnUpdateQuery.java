package co.ecso.jdao.database.query;

import co.ecso.jdao.database.ColumnList;
import co.ecso.jdao.database.cache.CacheKey;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * SingleColumnUpdateQuery.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 11.09.16
 */
@SuppressWarnings("WeakerAccess")
public final class SingleColumnUpdateQuery<T> implements Query<T> {

    private final String query;
    private final DatabaseField<T> whereColumn;
    private final T whereValue;
    private final Map<DatabaseField<?>, Object> values = new LinkedHashMap<>();

    public SingleColumnUpdateQuery(final String query, final DatabaseField<T> whereColumn, final T whereValue,
                                   final ColumnList values) {
        this.whereColumn = whereColumn;
        this.whereValue = whereValue;
        this.values.putAll(values.values());
        this.query = String.format(query, values.values().keySet().stream().map(l -> "%s = ?")
                .collect(Collectors.joining(",")));
    }

    @Override
    public String query() {
        return query;
    }

    @Override
    public CacheKey<T> getCacheKey() {
        return new CacheKey<>(
                whereColumn.valueClass(),
                whereColumn.name(),
                whereColumn.valueClass().getName(),
                whereColumn.sqlType(),
                (whereValue != null ? whereValue.toString() : ""),
                query,
                Arrays.toString(this.values().keySet().toArray()),
                Arrays.toString(this.values().values().toArray())
        );
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingleColumnUpdateQuery<?> that = (SingleColumnUpdateQuery<?>) o;
        return Objects.equals(query, that.query) &&
                Objects.equals(whereColumn, that.whereColumn) &&
                Objects.equals(whereValue, that.whereValue) &&
                Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, whereColumn, whereValue, values);
    }
}
