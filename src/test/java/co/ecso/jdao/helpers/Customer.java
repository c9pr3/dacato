package co.ecso.jdao.helpers;

import co.ecso.jdao.config.ApplicationConfig;
import co.ecso.jdao.database.ColumnList;
import co.ecso.jdao.database.DatabaseEntity;
import co.ecso.jdao.database.query.DatabaseField;
import co.ecso.jdao.database.query.DatabaseResultField;
import co.ecso.jdao.database.query.SingleColumnQuery;
import co.ecso.jdao.database.query.SingleColumnUpdateQuery;

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
public final class Customer implements DatabaseEntity<Long> {
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
    public Long primaryKey() {
        return this.id;
    }

    public CompletableFuture<DatabaseResultField<String>> firstName() {
        return this.findOne(new SingleColumnQuery<>(QUERY, Fields.FIRST_NAME, Fields.ID, this.primaryKey()), () ->
                this.objectValid);
    }

    public CompletableFuture<DatabaseResultField<Long>> number() {
        return this.findOne(new SingleColumnQuery<>(QUERY, Fields.NUMBER, Fields.ID, this.primaryKey()), () ->
                this.objectValid);
    }

    @Override
    public CompletableFuture<DatabaseEntity<Long>> save(final ColumnList columnValuesToSet) {
        final SingleColumnUpdateQuery<Long> query =
                new SingleColumnUpdateQuery<>("UPDATE customer SET %s WHERE %%s = ?", Fields.ID, id, columnValuesToSet);
        final CompletableFuture<Integer> updated = this.update(query, () -> this.objectValid);
        this.objectValid.set(false);
        return updated.thenApply(t -> new Customer(config, id));
    }

    @Override
    public ApplicationConfig config() {
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
