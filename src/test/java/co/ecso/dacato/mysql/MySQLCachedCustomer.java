package co.ecso.dacato.mysql;

import co.ecso.dacato.AbstractTest;
import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.database.CachedDatabaseEntity;
import co.ecso.dacato.database.ColumnList;
import co.ecso.dacato.database.cache.Cache;
import co.ecso.dacato.database.querywrapper.DatabaseField;
import co.ecso.dacato.database.querywrapper.DatabaseResultField;
import co.ecso.dacato.database.querywrapper.SingleColumnQuery;
import co.ecso.dacato.database.querywrapper.SingleColumnUpdateQuery;

import java.sql.Types;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * MySQLCachedCustomer.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @version $Id:$
 * @since 17.09.16
 */
final class MySQLCachedCustomer implements CachedDatabaseEntity<Long> {

    static final String TABLE_NAME = "customer";
    private static final String QUERY = String.format("SELECT %%s FROM %s WHERE id = ?", TABLE_NAME);
    private final ApplicationConfig config;
    private final Long id;
    private final AtomicBoolean objectValid = new AtomicBoolean(true);

    MySQLCachedCustomer(final ApplicationConfig config, final Long id) {
        this.config = config;
        this.id = id;
    }

    @Override
    public ApplicationConfig config() {
        return config;
    }

    @Override
    public Long primaryKey() {
        return id;
    }

    @Override
    public CompletableFuture<MySQLCachedCustomer> save(final ColumnList columnValuesToSet) {
        return this.update(new SingleColumnUpdateQuery<>(MySQLCachedCustomer.TABLE_NAME, "UPDATE " + TABLE_NAME + " SET %s WHERE %%s = ?",
                Fields.ID, this.id, columnValuesToSet), () -> objectValid).thenApply(rowsAffected ->
                new MySQLCachedCustomer(config, id));
    }

    @Override
    public Cache cache() {
        return AbstractTest.CACHE;
    }

    public CompletableFuture<DatabaseResultField<String>> firstName() {
        return this.findOne(new SingleColumnQuery<>(MySQLCachedCustomer.TABLE_NAME, QUERY, Fields.FIRST_NAME, Fields.ID, this.primaryKey()), () ->
                this.objectValid);
    }

    public CompletableFuture<DatabaseResultField<Long>> number() {
        return this.findOne(new SingleColumnQuery<>(MySQLCachedCustomer.TABLE_NAME, QUERY, Fields.NUMBER, Fields.ID, this.primaryKey()), () ->
                this.objectValid);
    }

    public static final class Fields {
        public static final DatabaseField<String> FIRST_NAME =
                new DatabaseField<>("customer_first_name", String.class, Types.VARCHAR);
        static final DatabaseField<Long> ID = new DatabaseField<>("id", Long.class, Types.BIGINT);
        static final DatabaseField<Long> NUMBER =
                new DatabaseField<>("customer_number", Long.class, Types.BIGINT);
    }
}
