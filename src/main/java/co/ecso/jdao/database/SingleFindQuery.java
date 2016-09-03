package co.ecso.jdao.database;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * SingleFindQuery.
 *
 * @param <T> What to Return.
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 03.09.16
 */
public final class SingleFindQuery<T> {

    private final DatabaseField<T> columnToSelect;
    private final Map<DatabaseField<?>, CompletableFuture<?>> whereFuture;
    private final String query;

    public SingleFindQuery(final String query, final DatabaseField<T> columnToSelect,
                           final Map<DatabaseField<?>, CompletableFuture<?>> whereFuture) {
        this.columnToSelect = columnToSelect;
        this.whereFuture = whereFuture;
        this.query = query;
    }

    public DatabaseField<T> columnSelect() {
        return columnToSelect;
    }

    public Map<DatabaseField<?>, CompletableFuture<?>> whereFuture() {
        return whereFuture;
    }

    public String query() {
        return query;
    }
}
