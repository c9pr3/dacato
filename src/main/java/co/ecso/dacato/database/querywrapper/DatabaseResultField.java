package co.ecso.dacato.database.querywrapper;

import java.io.Serializable;
import java.util.Objects;

/**
 * DatabaseResultField.
 *
 * @param <T> Field type, p.e. Long or String.
 * @author Christian Senkowski (cs@2scale.net)
 * @since 09.09.16
 */
public final class DatabaseResultField<T> implements Serializable {

    private static final long serialVersionUID = -8162450174026734384L;
    private final DatabaseField<T> selectedField;
    private final T resultValue;

    /**
     * Construct.
     *
     * @param selectedField Selected field.
     * @param resultValue   Result resultValue.
     */
    public DatabaseResultField(final DatabaseField<T> selectedField, final T resultValue) {
        this.selectedField = selectedField;
        this.resultValue = resultValue;
    }

    /**
     * Selected field.
     *
     * @return Selected field.
     */
    public DatabaseField<T> selectedField() {
        return selectedField;
    }

    /**
     * Result value.
     *
     * @return Result value.
     */
    public T resultValue() {
        return this.resultValue;
    }

    public Object resultValuePOJO() {
        return this.resultValue();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DatabaseResultField<?> that = (DatabaseResultField<?>) o;
        return Objects.equals(selectedField, that.selectedField) &&
                Objects.equals(resultValue, that.resultValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selectedField, resultValue);
    }

    @Override
    public String toString() {
        return "DatabaseResultField{" +
                "selectedField=" + selectedField +
                ", resultValue=" + resultValue +
                '}';
    }
}
