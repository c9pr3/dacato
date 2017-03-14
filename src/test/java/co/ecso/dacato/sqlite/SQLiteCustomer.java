package co.ecso.dacato.sqlite;

import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.database.ColumnList;
import co.ecso.dacato.database.DatabaseEntity;
import co.ecso.dacato.database.querywrapper.DatabaseField;
import co.ecso.dacato.database.querywrapper.DatabaseResultField;
import co.ecso.dacato.database.querywrapper.SingleColumnQuery;
import co.ecso.dacato.database.querywrapper.SingleColumnUpdateQuery;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SQLiteCustomer.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 11.10.16
 */
final class SQLiteCustomer implements DatabaseEntity<Integer> {
    static final String TABLE_NAME = "customer";
    private static final String QUERY = String.format("SELECT %%s FROM %s WHERE id = ?", TABLE_NAME);
    private final Integer id;
    private final ApplicationConfig config;
    private final AtomicBoolean objectValid = new AtomicBoolean(true);

    SQLiteCustomer(final ApplicationConfig config, final Integer id) {
        this.id = id;
        this.config = config;
    }

    @Override
    public Integer primaryKey() {
        return this.id;
    }

    public CompletableFuture<DatabaseResultField<String>> firstName() {
        return this.findOne(new SingleColumnQuery<>(SQLiteCustomer.TABLE_NAME, QUERY, Fields.FIRST_NAME, Fields.ID, this.primaryKey()), () ->
                this.objectValid);
    }

    public CompletableFuture<DatabaseResultField<Integer>> number() {
        return this.findOne(new SingleColumnQuery<>(SQLiteCustomer.TABLE_NAME, QUERY, Fields.NUMBER, Fields.ID, this.primaryKey()), () ->
                this.objectValid);
    }

    @Override
    public CompletableFuture<DatabaseEntity<Integer>> save(final ColumnList columnValuesToSet) {
        final SingleColumnUpdateQuery<Integer> query =
                new SingleColumnUpdateQuery<>(SQLiteCustomer.TABLE_NAME, "UPDATE customer SET %s WHERE %%s = ?", Fields.ID, id, columnValuesToSet);
        final CompletableFuture<Integer> updated = this.update(query, () -> this.objectValid);
        this.objectValid.set(false);
        return updated.thenApply(rowsAffected -> new SQLiteCustomer(config, id));
    }

    @Override
    public ApplicationConfig config() {
        return this.config;
    }

    @Override
    public int statementOptions() {
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

    public static final class Fields {
        public static final DatabaseField<Integer> ID = new DatabaseField<>("id", Integer.class, Types.BIGINT);
        public static final DatabaseField<String> FIRST_NAME =
                new DatabaseField<>("customer_first_name", String.class, Types.VARCHAR);
        static final DatabaseField<Integer> NUMBER =
                new DatabaseField<>("customer_number", Integer.class, Types.INTEGER);
    }
}
