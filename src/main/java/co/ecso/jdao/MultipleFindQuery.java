package co.ecso.jdao;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * MultipleFindQuery.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 02.09.16
 */
@SuppressWarnings("WeakerAccess")
public final class MultipleFindQuery {

    private final List<DatabaseField<?>> columnsToSelect;
    private final List<DatabaseField<?>> columnsWhere;
    private final CompletableFuture<?> whereFuture;
    private final String query;

    public MultipleFindQuery(final String query, final List<DatabaseField<?>> columnsToSelect,
                             final List<DatabaseField<?>> columnsWhere, final CompletableFuture<?> whereFuture) {
        this.columnsToSelect = columnsToSelect;
        this.columnsWhere = columnsWhere;
        this.whereFuture = whereFuture;
        this.query = query;
    }

    public MultipleFindQuery(final String query, final List<DatabaseField<?>> columnsToSelect) {
        this.columnsToSelect = columnsToSelect;
        this.columnsWhere = new LinkedList<>();
        this.whereFuture = CompletableFuture.completedFuture(null);
        this.query = query;
    }

    public List<DatabaseField<?>> columnsToSelect() {
        return columnsToSelect;
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
