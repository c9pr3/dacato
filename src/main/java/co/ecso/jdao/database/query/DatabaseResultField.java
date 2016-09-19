package co.ecso.jdao.database.query;

import java.util.Objects;

/**
 * DatabaseResultField.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 09.09.16
 */
public final class DatabaseResultField<T> {

    private final DatabaseField<T> selectedField;
    private final T result;

    public DatabaseResultField(final DatabaseField<T> selectedField, final T result) {
        this.selectedField = selectedField;
        this.result = result;
    }

    public DatabaseField<T> selectedField() {
        return selectedField;
    }

    public T value() {
        return this.result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatabaseResultField<?> that = (DatabaseResultField<?>) o;
        return Objects.equals(selectedField, that.selectedField) &&
                Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selectedField, result);
    }
}
