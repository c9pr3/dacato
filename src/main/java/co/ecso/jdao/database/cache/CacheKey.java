package co.ecso.jdao.database.cache;

import java.util.Objects;

/**
 * CacheKey.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 20.09.16
 */
public final class CacheKey {

    /**
     * Key.
     */
    private final int key;
    /**
     * Type Class.
     */
    private final Class<?> type;
    /**
     * Textual representation.
     */
    private final StringBuilder textKey = new StringBuilder();

    /**
     * Construct.
     *
     * @param type Type of cacheKey.
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
}
