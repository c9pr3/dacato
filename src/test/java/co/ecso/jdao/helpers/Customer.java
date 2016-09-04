package co.ecso.jdao.helpers;

import co.ecso.jdao.config.ApplicationConfig;
import co.ecso.jdao.database.DatabaseEntity;
import co.ecso.jdao.database.DatabaseField;

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
        return this.findById(QUERY, Fields.FIRST_NAME, Fields.ID);
    }

    public CompletableFuture<String> lastName() {
        this.checkValidity();
        return this.findById(QUERY, Fields.LAST_NAME, Fields.ID);
    }

    public CompletableFuture<Long> number() {
        this.checkValidity();
        return this.findById(QUERY, Fields.NUMBER, Fields.ID);
    }

    @Override
    public CompletableFuture<Customer> save(final Map<DatabaseField<?>, ?> updateMap,
                                            final Map<DatabaseField<?>, ?> whereMap) {
        this.checkValidity();
        return this.update(
                "UPDATE customer SET %s = ?, %s = ?, %s = ? WHERE %s = ?", updateMap, whereMap)
                .thenApply(b -> new Customer(this.config, this.id));
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
    public ApplicationConfig config() {
        return this.config;
    }

    public static final class Fields {
        public static final DatabaseField<Long> ID = new DatabaseField<>("id", -1L, Types.BIGINT);
        public static final DatabaseField<Long> NUMBER =
                new DatabaseField<>("customer_number", -1L, Types.BIGINT);
        public static final DatabaseField<String> FIRST_NAME =
                new DatabaseField<>("customer_first_name", "", Types.VARCHAR);
        public static final DatabaseField<String> LAST_NAME =
                new DatabaseField<>("customer_last_name", "", Types.VARCHAR);
    }
}
