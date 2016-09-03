package co.ecso.jdao.test;

import co.ecso.jdao.*;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
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
    private AtomicBoolean invalid = new AtomicBoolean(false);

    public Customer(final ApplicationConfig config, final long id) {
        this.id = id;
        this.config = config;
    }

    @Override
    public Long id() {
        this.checkValidity();
        return this.id;
    }

    public CompletableFuture<String> firstName() {
        this.checkValidity();
        return ((SingleColumnFinder<String>) () -> config).find(
                new SingleFindQuery<>(QUERY, Fields.FIRST_NAME,
                        new ColumnList().get(Fields.ID, CompletableFuture.completedFuture(this.id()))));
    }

    public CompletableFuture<String> lastName() {
        this.checkValidity();
        return ((SingleColumnFinder<String>) () -> config).find(
                new SingleFindQuery<>(QUERY, Fields.LAST_NAME,
                        new ColumnList().get(Fields.ID, CompletableFuture.completedFuture(this.id()))));
    }

    public CompletableFuture<Long> number() {
        this.checkValidity();
        return ((SingleColumnFinder<Long>) () -> config).find(
                new SingleFindQuery<>(QUERY, Fields.NUMBER,
                        new ColumnList().get(Fields.ID, CompletableFuture.completedFuture(this.id()))));
    }

    @Override
    public CompletableFuture<? extends DatabaseEntity> save(final Map<DatabaseField<?>, ?> map) {
        this.checkValidity();

        //TODO
        return null;
    }

    @Override
    public String toJson() throws SQLException {
        this.checkValidity();

        //TODO
        return "";
    }

    @Override
    public void checkValidity() {
        if (this.invalid.get()) {
            throw new RuntimeException("Object destroyed");
        }
    }

    @Override
    public co.ecso.jdao.ApplicationConfig config() {
        return this.config;
    }

    static final class Fields {
        static final DatabaseField<Long> ID = new DatabaseField<>("id", -1L, Types.BIGINT);
        static final DatabaseField<Long> NUMBER = new DatabaseField<>("customer_number", -1L, Types.BIGINT);
        static final DatabaseField<String> FIRST_NAME = new DatabaseField<>("customer_first_name", "", Types.VARCHAR);
        static final DatabaseField<String> LAST_NAME = new DatabaseField<>("customer_last_name", "", Types.VARCHAR);
    }
}
