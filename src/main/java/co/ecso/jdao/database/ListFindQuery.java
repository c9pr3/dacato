package co.ecso.jdao.database;

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
public final class ListFindQuery<T> {

    private final String query;
    private final DatabaseField<T> columnToSelect;
    private final Map<DatabaseField<?>, CompletableFuture<?>> whereFutureMap;

    public ListFindQuery(final String query, final DatabaseField<T> columnToSelect) {
        this.query = query;
        this.columnToSelect = columnToSelect;
        this.whereFutureMap = new LinkedHashMap<>();
    }

    public ListFindQuery(final String query, final DatabaseField<T> columnToSelect,
                         final Map<DatabaseField<?>, CompletableFuture<?>> whereFutureMap) {
        this.query = query;
        this.columnToSelect = columnToSelect;
        this.whereFutureMap = whereFutureMap;
    }

    public String query() {
        return query;
    }

    public DatabaseField<T> columnToSelect() {
        return columnToSelect;
    }

    public Map<DatabaseField<?>, CompletableFuture<?>> whereFutureMap() {
        return whereFutureMap;
    }
}
