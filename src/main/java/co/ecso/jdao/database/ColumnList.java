package co.ecso.jdao.database;

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
    private final List<DatabaseField<?>> keys = new LinkedList<>();
    private final List<Object> values = new LinkedList<>();

    public static List<DatabaseField<?>> build(final DatabaseField<?> ...fields) {
        return Collections.synchronizedList(Collections.unmodifiableList(Arrays.asList(fields)));
    }

    public static Map<DatabaseField<?>, CompletableFuture<?>> build(final DatabaseField<?> column,
                                                                    final CompletableFuture<?> future) {
        final Map<DatabaseField<?>, CompletableFuture<?>> rValMap = new LinkedHashMap<>();
        rValMap.put(column, future);
        return rValMap;
    }

    public ColumnList keys(final DatabaseField<?> ...keys) {
        this.keys.addAll(Arrays.asList(keys));
        return this;
    }

    public ColumnList values(final List<Object> value) {
        value.forEach(values::add);
        return this;
    }

    public Map<DatabaseField<?>, ?> build() {
        final Map<DatabaseField<?>, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i), values.get(i));
        }
        return map;
    }
}
