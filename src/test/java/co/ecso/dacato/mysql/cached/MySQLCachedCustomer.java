package co.ecso.dacato.mysql.cached;

import co.ecso.dacato.AbstractTest;
import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.database.CachedDatabaseEntity;
import co.ecso.dacato.database.ColumnList;
import co.ecso.dacato.database.cache.Cache;
import co.ecso.dacato.database.cache.CacheKey;
import co.ecso.dacato.database.query.DatabaseField;
import co.ecso.dacato.database.query.DatabaseResultField;
import co.ecso.dacato.database.query.SingleColumnQuery;
import co.ecso.dacato.database.query.SingleColumnUpdateQuery;

import java.sql.Types;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * MySQLCachedCustomer.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 17.09.16
 */
final class MySQLCachedCustomer implements CachedDatabaseEntity<Long> {

    private static final String TABLE_NAME = "customer";
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
        return this.update(new SingleColumnUpdateQuery<>("UPDATE " + TABLE_NAME + " SET %s WHERE %%s = ?",
                Fields.ID, this.id, columnValuesToSet), () -> objectValid).thenApply(rowsAffected ->
                new MySQLCachedCustomer(config, id));
    }

    @Override
    public Cache<CacheKey, CompletableFuture> cache() {
        return AbstractTest.CACHE;
    }

    public CompletableFuture<DatabaseResultField<String>> firstName() {
        return this.findOne(new SingleColumnQuery<>(QUERY, Fields.FIRST_NAME, Fields.ID, this.primaryKey()), () ->
                this.objectValid);
    }

    public CompletableFuture<DatabaseResultField<Long>> number() {
        return this.findOne(new SingleColumnQuery<>(QUERY, Fields.NUMBER, Fields.ID, this.primaryKey()), () ->
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
