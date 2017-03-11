package co.ecso.dacato.database.querywrapper;

import co.ecso.dacato.database.ColumnList;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * MultiColumnSelectQuery.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @since 25.09.16
 */
public final class MultiColumnSelectQuery<T> implements Query<T> {

    private static final long serialVersionUID = 7418061210484515561L;
    private final String query;
    private final List<DatabaseField<?>> columnsToSelect;
    private final ColumnList whereColumnValues;

    /**
     * Construct.
     *
     * @param query             Query to execute, p.e. select %s, %s from table_x where %s = ? and (%s = ? or %s = ?)
     * @param columnsToSelect   Columns to select.
     * @param whereColumnValues Column plus columnValuesToSet - map.
     */
    public MultiColumnSelectQuery(final String query, final List<DatabaseField<?>> columnsToSelect,
                                  final ColumnList whereColumnValues) {
        this.query = query;
        this.columnsToSelect = columnsToSelect;
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

    /**
     * Columns to select.
     *
     * @return Columns to select.
     */
    public List<DatabaseField<?>> columnsToSelect() {
        return columnsToSelect;
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
    public String toString() {
        return String.valueOf(Arrays.asList(
                columnsToSelect.stream().map(DatabaseField::valueClass),
                query,
                columnsToSelect.stream().map(DatabaseField::name),
                columnsToSelect.stream().map(l -> l.valueClass().getName()),
                columnsToSelect.stream().map(DatabaseField::sqlType),
                Arrays.toString(this.whereColumnValues.values().keySet().toArray()),
                Arrays.toString(this.whereColumnValues.values().values().toArray())
        ));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MultiColumnSelectQuery<?> that = (MultiColumnSelectQuery<?>) o;
        return Objects.equals(query, that.query) &&
                Objects.equals(columnsToSelect, that.columnsToSelect) &&
                Objects.equals(whereColumnValues, that.whereColumnValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, columnsToSelect, whereColumnValues);
    }
}
