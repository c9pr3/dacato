package co.ecso.jdao;

import java.util.LinkedList;
import java.util.List;
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
    private final List<DatabaseField<?>> columnsWhere;
    private final CompletableFuture<?> whereFuture;
    private final String query;

    public ListFindQuery(final String query, final DatabaseField<T> columnToSelect,
                         final List<DatabaseField<?>> columnsWhere, final CompletableFuture<?> whereFuture) {
        this.columnToSelect = columnToSelect;
        this.columnsWhere = columnsWhere;
        this.whereFuture = whereFuture;
        this.query = query;
    }

    public ListFindQuery(final String query, final DatabaseField<T> columnToSelect,
                         final List<DatabaseField<?>> columnsWhere) {
        this.columnToSelect = columnToSelect;
        this.columnsWhere = columnsWhere;
        this.whereFuture = CompletableFuture.completedFuture(null);
        this.query = query;
    }

    public ListFindQuery(final String query, final DatabaseField<T> columnToSelect) {
        this.columnToSelect = columnToSelect;
        this.columnsWhere = new LinkedList<>();
        this.whereFuture = CompletableFuture.completedFuture(null);
        this.query = query;
    }

    public DatabaseField<T> columnSelect() {
        return columnToSelect;
    }

    public List<DatabaseField<?>> columnsWhere() {
        return columnsWhere;
    }

    public CompletableFuture<?> whereFuture() {
        return whereFuture;
    }

    public String query() {
        return query;
    }
}
