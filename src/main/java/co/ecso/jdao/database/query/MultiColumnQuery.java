package co.ecso.jdao.database.query;

import co.ecso.jdao.database.ColumnList;

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
}
