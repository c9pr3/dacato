package co.ecso.dacato.database.cache;

import java.io.Serializable;
import java.util.Objects;

/**
 * CacheKey.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @since 20.09.16
 */
public final class CacheKey implements Serializable {

    private static final long serialVersionUID = -5589630047137523865L;
    private final int key;
    private final Class<?> type;
    private final StringBuilder textKey = new StringBuilder();

    /**
     * Construct.
     *
     * @param type   Type of cacheKey.
     * @param values Value-Objects to hash in order to make this cache key individual.
     */
    public CacheKey(final Class<?> type, final Object... values) {
        this.type = type;
        this.key = Objects.hash(values);
        for (final Object value : values) {
            if (value != null) {
                this.textKey.append(value.toString());
            }
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CacheKey cacheKey = (CacheKey) o;
        return key == cacheKey.key &&
                Objects.equals(type, cacheKey.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, type);
    }

    /**
     * Key.
     *
     * @return Key.
     */
    public int key() {
        return key;
    }

    /**
     * Type.
     *
     * @return Type class.
     */
    public Class<?> type() {
        return type;
    }

    /**
     * Text key.
     *
     * @return Textual representation of this cacheKey.
     */
    public StringBuilder textKey() {
        return textKey;
    }

    @Override
    public String toString() {
        return "CacheKey{" +
                "key=" + key +
                ", type=" + type +
                ", textKey=" + textKey +
                '}';
    }
}
