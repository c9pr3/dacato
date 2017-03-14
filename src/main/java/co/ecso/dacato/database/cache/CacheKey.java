package co.ecso.dacato.database.cache;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * CacheKey.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @since 01.03.17
 */
public final class CacheKey<T> implements Serializable {

    private static final long serialVersionUID = -3419490430350543746L;
    private final Object[] keys;
    private final int hashCode;

    public CacheKey(final Object ...keys) {
        this.keys = keys.clone();
        this.hashCode = 31 * Arrays.hashCode(keys) + this.getClass().getName().hashCode();
    }

    public boolean hasKey(final T key) {
        Objects.requireNonNull(keys);
        Objects.requireNonNull(key);
        return Arrays.asList(keys).contains(key);
    }

    @Override
    public String toString() {
        return "CacheKey{"
                + "keys=" + Arrays.toString(keys)
                + ", hashCode=" + hashCode
                + '}';
    }
}
