package co.ecso.dacato.helpers;

import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.database.ColumnList;
import co.ecso.dacato.database.DatabaseEntity;
import co.ecso.dacato.database.query.DatabaseField;
import co.ecso.dacato.database.query.DatabaseResultField;
import co.ecso.dacato.database.query.SingleColumnQuery;
import co.ecso.dacato.database.query.SingleColumnUpdateQuery;

import java.sql.Types;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Customer.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 29.08.16
 */
@SuppressWarnings("WeakerAccess")
public class Customer implements DatabaseEntity<Long> {
    private static final String TABLE_NAME = "customer";
    private static final String QUERY = String.format("SELECT %%s FROM %s WHERE id = ?", TABLE_NAME);
    private final Long id;
    private final ApplicationConfig config;
    private AtomicBoolean objectValid = new AtomicBoolean(true);

    public Customer(final ApplicationConfig config, final Long id) {
        this.id = id;
        this.config = config;
    }

    @Override
    public final Long primaryKey() {
        return this.id;
    }

    public final CompletableFuture<DatabaseResultField<String>> firstName() {
        return this.findOne(new SingleColumnQuery<>(QUERY, Fields.FIRST_NAME, Fields.ID, this.primaryKey()), () ->
                this.objectValid);
    }

    public final CompletableFuture<DatabaseResultField<Long>> number() {
        return this.findOne(new SingleColumnQuery<>(QUERY, Fields.NUMBER, Fields.ID, this.primaryKey()), () ->
                this.objectValid);
    }

    @Override
    public final CompletableFuture<DatabaseEntity<Long>> save(final ColumnList columnValuesToSet) {
        final SingleColumnUpdateQuery<Long> query =
                new SingleColumnUpdateQuery<>("UPDATE customer SET %s WHERE %%s = ?", Fields.ID, id, columnValuesToSet);
        final CompletableFuture<Integer> updated = this.update(query, () -> this.objectValid);
        this.objectValid.set(false);
        return updated.thenApply(rowsAffected -> new Customer(config, id));
    }

    @Override
    public final ApplicationConfig config() {
        return this.config;
    }

    public static final class Fields {
        public static final DatabaseField<Long> ID = new DatabaseField<>("id", Long.class, Types.BIGINT);
        public static final DatabaseField<Long> NUMBER =
                new DatabaseField<>("customer_number", Long.class, Types.BIGINT);
        public static final DatabaseField<String> FIRST_NAME =
                new DatabaseField<>("customer_first_name", String.class, Types.VARCHAR);
    }
}
