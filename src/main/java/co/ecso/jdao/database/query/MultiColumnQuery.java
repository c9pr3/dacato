package co.ecso.jdao.database.query;

import co.ecso.jdao.database.ColumnList;
import co.ecso.jdao.database.cache.CacheKey;

import java.util.Arrays;
import java.util.Objects;

/**
 * MultiColumnQuery.
 *
 * @param <T> Type of return, p.e. Long.
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 13.09.16
 */
public final class MultiColumnQuery<T> implements Query<T> {

    /**
     * Query string.
     */
    private final String query;
    /**
     * Column to select.
     */
    private final DatabaseField<T> columnToSelect;
    /**
     * Where columns and values.
     */
    private final ColumnList whereColumnValues;

    /**
     * Construct.
     *
     * @param query Query to execute, p.e. select %s from table_x where %s = ? and (%s = ? or %s = ?)
     * @param columnToSelect Column to select.
     * @param whereColumnValues Column plus columnValuesToSet - map.
     */
    public MultiColumnQuery(final String query, final DatabaseField<T> columnToSelect,
                            final ColumnList whereColumnValues) {
        this.query = query;
        this.columnToSelect = columnToSelect;
        this.whereColumnValues = whereColumnValues;
    }

    @Override
    public String query() {
        return query;
    }

    @Override
    public CacheKey<T> getCacheKey() {
        return new CacheKey<>(
                columnToSelect.valueClass(),
                query,
                columnToSelect.name(),
                columnToSelect.valueClass().getName(),
                columnToSelect.sqlType(),
                Arrays.toString(this.whereColumnValues.values().keySet().toArray()),
                Arrays.toString(this.whereColumnValues.values().values().toArray())
        );
    }

    /**
     * Column to select.
     *
     * @return Column to select.
     */
    public DatabaseField<T> columnToSelect() {
        return columnToSelect;
    }

    /**
     * Where column resultValue map.
     *
     * @return Where column resultValue map
     */
    public ColumnList values() {
        return whereColumnValues;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MultiColumnQuery<?> that = (MultiColumnQuery<?>) o;
        return Objects.equals(query, that.query) &&
                Objects.equals(columnToSelect, that.columnToSelect) &&
                Objects.equals(whereColumnValues, that.whereColumnValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, columnToSelect, whereColumnValues);
    }
}
