package co.ecso.jdao;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * ListFindQuery.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 02.09.16
 */
@SuppressWarnings("WeakerAccess")
public final class ListFindQuery<T> {

    private final DatabaseField<T> columnToSelect;
    private final Map<DatabaseField<?>, CompletableFuture<?>> columnsWhere;
    private final String query;

    public ListFindQuery(final String query, final DatabaseField<T> columnToSelect,
                         final Map<DatabaseField<?>, CompletableFuture<?>> columnsWhere) {
        this.columnToSelect = columnToSelect;
        this.columnsWhere = columnsWhere;
        this.query = query;
    }

    public ListFindQuery(final String query, final DatabaseField<T> columnToSelect) {
        this.columnToSelect = columnToSelect;
        this.columnsWhere = new LinkedHashMap<>();
        this.query = query;
    }

    public DatabaseField<T> columnSelect() {
        return columnToSelect;
    }

    public Map<DatabaseField<?>, CompletableFuture<?>> columnsWhere() {
        return columnsWhere;
    }

    public String query() {
        return query;
    }
}
