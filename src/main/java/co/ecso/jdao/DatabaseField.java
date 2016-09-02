package co.ecso.jdao;

/**
 * DatabaseField
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 08.08.16
 */
@SuppressWarnings("WeakerAccess")
public final class DatabaseField<T> {

    private final String name;
    private final T defaultValue;
    private final int sqlType;
    private final T value;

    public DatabaseField(final String name, final T defaultValue, final int sqlType) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.sqlType = sqlType;
        this.value = null;
    }

    public DatabaseField(final String name, final T defaultValue, final int sqlType, final T value) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.sqlType = sqlType;
        this.value = value;
    }

    @Override
    public String toString() {
        return name;
    }

    public Class<?> valueClass() {
        return defaultValue.getClass();
    }

    public int sqlType() {
        return sqlType;
    }

    public T value() {
        return value;
    }
}
