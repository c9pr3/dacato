package co.ecso.jdao;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * ColumnList.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 02.09.16
 */
public final class ColumnList {
    public List<DatabaseField<?>> get(final DatabaseField<?> ...fields) {
        return Collections.synchronizedList(Collections.unmodifiableList(Arrays.asList(fields)));
    }

    public Map<DatabaseField<?>, CompletableFuture<?>> get(final DatabaseField<?> column,
                                                           final CompletableFuture<?> future) {
        final Map<DatabaseField<?>, CompletableFuture<?>> rValMap = new LinkedHashMap<>();
        rValMap.put(column, future);
        return rValMap;
    }
}
