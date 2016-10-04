package co.ecso.dacato.database.query;

import java.util.Objects;

/**
 * DatabaseField.
 *
 * @param <T> Type of field, for "id" p.e. Long, for "name" String etc.
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 09.09.16
 */
public final class DatabaseField<T> {

    /**
     * Field name.
     */
    private final String name;
    /**
     * Field value class.
     */
    private final Class<T> valueClass;
    /**
     * Field SQL Type.
     */
    private final int sqlType;

    /**
     * Construct.
     *
     * @param name       Field name, p.e. "id".
     * @param valueClass Value class, p.e. Long.
     * @param sqlType    SQLType, p.e. Types.BIGINT.
     * @see java.sql.Types
     */
    public DatabaseField(final String name, final Class<T> valueClass, final int sqlType) {
        this.name = name;
        this.valueClass = valueClass;
        this.sqlType = sqlType;
    }

    /**
     * Field name, p.e. "id".
     *
     * @return Field name.
     */
    public String name() {
        return name;
    }

    /**
     * Field value class, p.e. Long.class.
     *
     * @return Value class.
     */
    public Class<T> valueClass() {
        return this.valueClass;
    }

    /**
     * SQL Type, p.e. Types.BIGINT.
     *
     * @return SQL Type.
     * @see java.sql.Types
     */
    public int sqlType() {
        return this.sqlType;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DatabaseField<?> that = (DatabaseField<?>) o;
        return sqlType == that.sqlType &&
                Objects.equals(name, that.name) &&
                Objects.equals(valueClass, that.valueClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, valueClass, sqlType);
    }
}
