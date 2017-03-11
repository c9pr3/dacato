package co.ecso.dacato.database.querywrapper;

import co.ecso.dacato.database.ColumnList;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * SingleColumnUpdateQuery.
 *
 * @param <T> Type of query -> type of where.
 * @author Christian Senkowski (cs@2scale.net)
 * @since 11.09.16
 */
public final class SingleColumnUpdateQuery<T> implements Query<T> {

    private static final long serialVersionUID = 2004667026197234402L;
    private final String query;
    private final DatabaseField<T> whereColumn;
    private final T whereValue;
    private final Map<DatabaseField<?>, Object> columnValuesToSet = new LinkedHashMap<>();

    /**
     * Construct.
     *
     * @param query             Query to execute, p.e. update table_x set %s = ?, %s = ? where %s = ?
     * @param whereColumn       Where column.
     * @param whereValue        Where value.
     * @param columnValuesToSet Columns and values to set.
     */
    public SingleColumnUpdateQuery(final String query, final DatabaseField<T> whereColumn, final T whereValue,
                                   final ColumnList columnValuesToSet) {
        this.whereColumn = whereColumn;
        this.whereValue = whereValue;
        this.columnValuesToSet.putAll(columnValuesToSet.values());
        this.query = String.format(query, columnValuesToSet.values().keySet().stream().map(l -> "%s = ?")
                .collect(Collectors.joining(",")));
    }

    @Override
    public String query() {
        return query;
    }

    @Override
    public Class<T> queryType() {
        return whereColumn.valueClass();
    }

    @Override
    public String toString() {
        final String value;
        if (whereValue != null) {
            value = whereValue.toString();
        } else {
            value = "";
        }
        return String.valueOf(Arrays.asList(
                whereColumn.valueClass(),
                whereColumn.name(),
                whereColumn.valueClass().getName(),
                whereColumn.sqlType(),
                value,
                query,
                Arrays.toString(this.columnValuesToSet().keySet().toArray()),
                Arrays.toString(this.columnValuesToSet().values().toArray())
        ));
    }

    /**
     * Where column.
     *
     * @return Where column.
     */
    public DatabaseField<T> whereColumn() {
        return whereColumn;
    }

    /**
     * Where value.
     *
     * @return Where value.
     */
    public T whereValue() {
        return whereValue;
    }

    /**
     * Column and values to set.
     *
     * @return ColumnList.
     */
    public Map<DatabaseField<?>, ?> columnValuesToSet() {
        return columnValuesToSet;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SingleColumnUpdateQuery<?> that = (SingleColumnUpdateQuery<?>) o;
        return Objects.equals(query, that.query) &&
                Objects.equals(whereColumn, that.whereColumn) &&
                Objects.equals(whereValue, that.whereValue) &&
                Objects.equals(columnValuesToSet, that.columnValuesToSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, whereColumn, whereValue, columnValuesToSet);
    }
}
