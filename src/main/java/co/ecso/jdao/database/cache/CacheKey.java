package co.ecso.jdao.database.cache;

import java.util.Objects;

/**
 * CacheKey.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 20.09.16
 */
public final class CacheKey<T> {
    private final int key;
    private final Class<T> type;

    public CacheKey(final Class<T> type, final Object ...values) {
        this.type = type;
        this.key = Objects.hash(values);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CacheKey<?> cacheKey = (CacheKey<?>) o;
        return key == cacheKey.key &&
                Objects.equals(type, cacheKey.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, type);
    }
}
