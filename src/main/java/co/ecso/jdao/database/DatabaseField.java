package co.ecso.jdao.database;

/**
 * DatabaseField.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 09.09.16
 */
@SuppressWarnings("WeakerAccess")
public final class DatabaseField<T> {

    private final String name;
    private final Class<T> valueClass;
    private final int sqlType;

    public DatabaseField(final String name, final Class<T> valueClass, final int sqlType) {
        this.name = name;
        this.valueClass = valueClass;
        this.sqlType = sqlType;
    }

    public String name() {
        return name;
    }

    public Class<T> valueClass() {
        return this.valueClass;
    }

    public int sqlType() {
        return this.sqlType;
    }

    @Override
    public String toString() {
        return name;
    }
}
