package co.ecso.jdao.cache;

import java.io.Serializable;
import java.util.Objects;

/**
 * CacheKey.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 06.09.16
 */
public final class CacheKey<T> implements Serializable {

    private static final long serialVersionUID = -384732894789324L;

    private final String query;
//    private final DatabaseField<?> columnName;
//    private final transient CompletableFuture<T> whereId;
//    private final transient Map<DatabaseField<?>, ?> values;

    CacheKey(final String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "CacheKey{" +
                "query='" + query + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CacheKey<?> cacheKey = (CacheKey) o;
        return Objects.equals(query, cacheKey.query);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query);
    }
}
