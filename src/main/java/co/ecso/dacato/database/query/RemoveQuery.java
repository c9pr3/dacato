package co.ecso.dacato.database.query;

import co.ecso.dacato.database.ColumnList;

import java.util.Arrays;
import java.util.Objects;

/**
 * RemoveQuery.
 *
 * @param <T> Type of Query, p.e. Long.
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 01.10.16
 */
public final class RemoveQuery<T> implements Query<T> {

    /**
     * Query string.
     */
    private final String query;
    /**
     * Where columns and values.
     */
    private final ColumnList whereColumnValues;

    /**
     * Construct.
     *
     * @param query             Query to execute, p.e. select %s from table_x where %s = ? and (%s = ? or %s = ?)
     * @param whereColumnValues Column plus columnValuesToSet - map.
     */
    public RemoveQuery(final String query, final ColumnList whereColumnValues) {
        this.query = query;
        this.whereColumnValues = whereColumnValues;
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
