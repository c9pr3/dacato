package co.ecso.dacato.database.querywrapper;

import java.util.Collections;
import java.util.Objects;

/**
 * TruncateQuery.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @since 14.03.17
 */
public final class TruncateQuery<T> implements Query<T> {
    private static final long serialVersionUID = -7975766286210264241L;
    private final String query;
    private final String tableName;

    /**
     * Construct.
     *
     * @param query Query to execute, p.e. DELETE from table_x where %s = ? and (%s = ? or %s = ?)
     */
    public TruncateQuery(final String tableName, final String query) {
        this.query = query;
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
        return String.valueOf(Collections.singletonList(query));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TruncateQuery<?> that = (TruncateQuery<?>) o;
        return Objects.equals(query, that.query) &&
                Objects.equals(tableName, that.tableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, tableName);
    }
}
