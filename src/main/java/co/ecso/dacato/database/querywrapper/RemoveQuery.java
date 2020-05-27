package co.ecso.dacato.database.querywrapper;

import co.ecso.dacato.database.ColumnList;

import java.util.Arrays;
import java.util.Objects;

/**
 * RemoveQuery.
 *
 * @param <T> Type of Query, p.e. Long.
 * @author Christian Scharmach (cs@e-cs.co)
 * @since 01.10.16
 */
public final class RemoveQuery<T> implements Query<T> {

    private static final long serialVersionUID = -7975766286210264241L;
    private final String query;
    private final ColumnList whereColumnValues;
    private final String tableName;

    /**
     * Construct.
     *
     * @param query Query to execute, p.e. DELETE from table_x where %s = ? and (%s = ? or %s = ?)
     * @param whereColumnValues Column plus columnValuesToSet - map.
     */
    public RemoveQuery(final String tableName, final String query, final ColumnList whereColumnValues) {
        this.query = query;
        this.whereColumnValues = whereColumnValues;
        this.tableName = tableName;
    }

    @Override
    public String tableName() {
        return this.tableName;
    }

    @Override
    public String query() {
        return query;
    }

    @Override
    public Class<T> queryType() {
        return null;
    }

    @Override
    public String toString() {
        return String.valueOf(Arrays.asList(
                query,
                Arrays.toString(this.whereColumnValues.values().keySet().toArray()),
                Arrays.toString(this.whereColumnValues.values().values().toArray())
        ));
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
        final RemoveQuery<?> that = (RemoveQuery<?>) o;
        return Objects.equals(query, that.query) &&
                Objects.equals(whereColumnValues, that.whereColumnValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, whereColumnValues);
    }
}
