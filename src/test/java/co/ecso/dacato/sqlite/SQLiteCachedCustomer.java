package co.ecso.dacato.sqlite;

import co.ecso.dacato.AbstractTest;
import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.database.CachedDatabaseEntity;
import co.ecso.dacato.database.ColumnList;
import co.ecso.dacato.database.cache.Cache;
import co.ecso.dacato.database.querywrapper.DatabaseField;
import co.ecso.dacato.database.querywrapper.DatabaseResultField;
import co.ecso.dacato.database.querywrapper.SingleColumnQuery;
import co.ecso.dacato.database.querywrapper.SingleColumnUpdateQuery;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SQLiteCachedCustomer.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 17.09.16
 */
final class SQLiteCachedCustomer implements CachedDatabaseEntity<Integer> {

    static final String TABLE_NAME = "customer";
    private static final String QUERY = String.format("SELECT %%s FROM %s WHERE id = ?", TABLE_NAME);
    private final ApplicationConfig config;
    private final Integer id;
    private final AtomicBoolean objectValid = new AtomicBoolean(true);

    SQLiteCachedCustomer(final ApplicationConfig config, final Integer id) {
        this.config = config;
        this.id = id;
    }

    @Override
    public ApplicationConfig config() {
        return config;
    }

    @Override
    public Integer primaryKey() {
        return id;
    }

    @Override
    public CompletableFuture<SQLiteCachedCustomer> save(final ColumnList columnValuesToSet) {
        return this.update(new SingleColumnUpdateQuery<>(SQLiteCachedCustomer.QUERY, "UPDATE " + TABLE_NAME + " SET %s WHERE %%s = ?",
                Fields.ID, this.id, columnValuesToSet), () -> objectValid).thenApply(rowsAffected ->
                new SQLiteCachedCustomer(config, id));
    }

    @Override
    public Cache cache() {
        return AbstractTest.CACHE;
    }

    public CompletableFuture<DatabaseResultField<String>> firstName() {
        return this.findOne(new SingleColumnQuery<>(SQLiteCachedCustomer.TABLE_NAME, QUERY, Fields.FIRST_NAME, Fields.ID, this.primaryKey()), () ->
                this.objectValid);
    }

    public CompletableFuture<DatabaseResultField<Integer>> number() {
        return this.findOne(new SingleColumnQuery<>(SQLiteCachedCustomer.TABLE_NAME, QUERY, Fields.NUMBER, Fields.ID, this.primaryKey()), () ->
                this.objectValid);
    }

    @Override
    public int statementOptions() {
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

    public static final class Fields {
        public static final DatabaseField<String> FIRST_NAME =
                new DatabaseField<>("customer_first_name", String.class, Types.VARCHAR);
        static final DatabaseField<Integer> ID = new DatabaseField<>("id", Integer.class, Types.INTEGER);
        static final DatabaseField<Integer> NUMBER =
                new DatabaseField<>("customer_number", Integer.class, Types.INTEGER);
    }
}
