package co.ecso.jdao.database.query;

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
}
