package co.ecso.jdao.database;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * MultipleFindQuery.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 02.09.16
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class MultipleFindQuery {

    private final List<DatabaseField<?>> columnsToSelect;
    private final Map<DatabaseField<?>, CompletableFuture<?>> columnsWhere;
    private final String query;

    public MultipleFindQuery(final String query, final List<DatabaseField<?>> columnsToSelect,
                             final Map<DatabaseField<?>, CompletableFuture<?>> columnsWhere) {
        this.columnsToSelect = columnsToSelect;
        this.columnsWhere = columnsWhere;
        this.query = query;
    }

    public MultipleFindQuery(final String query, final List<DatabaseField<?>> columnsToSelect) {
        this.columnsToSelect = columnsToSelect;
        this.columnsWhere = new LinkedHashMap<>();
        this.query = query;
    }

    public List<DatabaseField<?>> columnsToSelect() {
        return columnsToSelect;
    }

    public Map<DatabaseField<?>, CompletableFuture<?>> columnsWhere() {
        return columnsWhere;
    }

    public String query() {
        return query;
    }

}
