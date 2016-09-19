package co.ecso.jdao.database.query;

import co.ecso.jdao.database.ColumnList;

import java.util.Objects;

/**
 * MultiColumnQuery.
 *
 * @param <T> Type of return, p.e. Long.
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 13.09.16
 */
@SuppressWarnings("WeakerAccess")
public final class MultiColumnQuery<T> implements Query<T> {

    private final String query;
    private final DatabaseField<T> columnToSelect;
    private final ColumnList values;

    public MultiColumnQuery(final String query, final DatabaseField<T> columnToSelect, final ColumnList values) {
        this.query = query;
        this.columnToSelect = columnToSelect;
        this.values = values;
    }

    @Override
    public String query() {
        return query;
    }

    public DatabaseField<T> columnToSelect() {
        return columnToSelect;
    }

    public ColumnList values() {
        return values;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultiColumnQuery<?> that = (MultiColumnQuery<?>) o;
        return Objects.equals(query, that.query) &&
                Objects.equals(columnToSelect, that.columnToSelect) &&
                Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, columnToSelect, values);
    }
}
