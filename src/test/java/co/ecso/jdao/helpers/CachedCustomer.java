package co.ecso.jdao.helpers;

import co.ecso.jdao.AbstractTest;
import co.ecso.jdao.config.ApplicationConfig;
import co.ecso.jdao.database.CachedDatabaseEntity;
import co.ecso.jdao.database.ColumnList;
import co.ecso.jdao.database.DatabaseEntity;
import co.ecso.jdao.database.cache.Cache;
import co.ecso.jdao.database.query.DatabaseField;
import co.ecso.jdao.database.query.DatabaseResultField;
import co.ecso.jdao.database.query.Query;
import co.ecso.jdao.database.query.SingleColumnQuery;

import java.sql.SQLException;
import java.sql.Types;
import java.util.concurrent.CompletableFuture;

/**
 * CachedCustomer.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 17.09.16
 */
public final class CachedCustomer implements CachedDatabaseEntity<Long> {

    private final ApplicationConfig config;
    private final Long id;
    private static final String TABLE_NAME = "customer";
    private static final String QUERY = String.format("SELECT %%s FROM %s WHERE id = ?", TABLE_NAME);

    public CachedCustomer(final ApplicationConfig config, final Long id) {
        this.config = config;
        this.id = id;
    }

    @Override
    public ApplicationConfig config() {
        return config;
    }

    @Override
    public Long id() {
        return id;
    }

    @Override
    public CompletableFuture<? extends DatabaseEntity<Long>> save(final ColumnList values) {
        return null;
    }

    @Override
    public String toJson() throws SQLException {
        return null;
    }

    @Override
    public void checkValidity() {

    }

    @Override
    public Cache<Query<Long>, CompletableFuture<DatabaseResultField<Long>>> cache() {
        return AbstractTest.CACHE;
    }

    public CompletableFuture<DatabaseResultField<String>> firstName() {
        this.checkValidity();
        return this.findOne(new SingleColumnQuery<>(QUERY, Fields.FIRST_NAME, Fields.ID, this.id()));
    }

    public CompletableFuture<DatabaseResultField<String>> lastName() {
        this.checkValidity();
        return this.findOne(new SingleColumnQuery<>(QUERY, Fields.LAST_NAME, Fields.ID, this.id()));
    }

    public CompletableFuture<DatabaseResultField<Long>> number() {
        this.checkValidity();
        return this.findOne(new SingleColumnQuery<>(QUERY, Fields.NUMBER, Fields.ID, this.id()));
    }

    public static final class Fields {
        public static final DatabaseField<Long> ID = new DatabaseField<>("id", Long.class, Types.BIGINT);
        public static final DatabaseField<Long> NUMBER =
                new DatabaseField<>("customer_number", Long.class, Types.BIGINT);
        public static final DatabaseField<String> FIRST_NAME =
                new DatabaseField<>("customer_first_name", String.class, Types.VARCHAR);
        public static final DatabaseField<String> LAST_NAME =
                new DatabaseField<>("customer_last_name", String.class, Types.VARCHAR);
    }
}
