package co.ecso.dacato.database.querywrapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * InsertQuery.
 *
 * @param <T> Return type of query, p.e. Long for id, String for name etc.
 * @author Christian Senkowski (cs@2scale.net)
 * @since 12.09.16
 */
public final class InsertQuery<T> implements Query<T> {

    private static final long serialVersionUID = -5438962181365261584L;
    private final String query;
    private final Map<DatabaseField<?>, Object> columnValueMap = new LinkedHashMap<>();
    private final DatabaseField<T> columnToReturn;
    private final boolean returnGeneratedKey;

    /**
     * Construct.
     *
     * @param query Query to execute, p.e. insert into table_x (%s, %s, %s) columnValuesToSet (?, ?, ?)
     */
    public InsertQuery(@SuppressWarnings("SameParameterValue") final String query) {
        this.query = query;
        this.returnGeneratedKey = false;
        this.columnToReturn = null;
    }

    /**
     * Construct.
     *
     * @param query          Query to execute, p.e. insert into table_x (%s, %s, %s) columnValuesToSet (?, ?, ?)
     * @param columnToReturn Column to return, mostly "id"
     */
    public InsertQuery(final String query, final DatabaseField<T> columnToReturn) {
        this.query = query;
        this.columnToReturn = columnToReturn;
        this.returnGeneratedKey = true;
    }

    /**
     * Add a field.
     *
     * @param field Field to add.
     * @param value Value to add.
     * @param <R>   Field-type and resultValue needs to be the same but not necessarily <T>.
     */
    public <R> void add(final DatabaseField<R> field, final R value) {
        columnValueMap.put(field, value);
    }

    @Override
    public String query() {
        return query;
    }

    @Override
    public Class<T> queryType() {
        if (columnToReturn != null) {
            return columnToReturn.valueClass();
        } else {
            return null;
        }
    }

    /**
     * Values.
     *
     * @return Values.
     */
    public Map<DatabaseField<?>, ?> values() {
        return columnValueMap;
    }

    /**
     * Column to return.
     *
     * @return Column to return.
     */
    public DatabaseField<T> columnToReturn() {
        return columnToReturn;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final InsertQuery<?> that = (InsertQuery<?>) o;
        return Objects.equals(query, that.query) &&
                Objects.equals(columnValueMap, that.columnValueMap) &&
                Objects.equals(columnToReturn, that.columnToReturn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, columnValueMap, columnToReturn);
    }

    /**
     * Return Generated key or not.
     *
     * @return true if query should return generated key.
     */
    public boolean returnGeneratedKey() {
        return this.returnGeneratedKey;
    }

    @Override
    public String toString() {
        return "InsertQuery{" +
                "query='" + query + '\'' +
                ", columnValueMap=" + columnValueMap +
                ", columnToReturn=" + columnToReturn +
                ", returnGeneratedKey=" + returnGeneratedKey +
                '}';
    }
}
