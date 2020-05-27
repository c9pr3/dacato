package co.ecso.dacato.cassandra;

import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.database.ColumnList;
import co.ecso.dacato.database.DatabaseEntity;
import co.ecso.dacato.database.querywrapper.DatabaseField;
import co.ecso.dacato.database.querywrapper.DatabaseResultField;
import co.ecso.dacato.database.querywrapper.SingleColumnQuery;
import co.ecso.dacato.database.querywrapper.SingleColumnUpdateQuery;

import java.sql.Types;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Customer.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @version $Id:$
 * @since 29.08.16
 */
final class CassandraCustomer implements DatabaseEntity<byte[]> {
    static final String TABLE_NAME = "customer";
    private static final String QUERY = String.format("SELECT %%s FROM %s WHERE id = ?", TABLE_NAME);
    private final byte[] id;
    private final ApplicationConfig config;
    private final AtomicBoolean objectValid = new AtomicBoolean(true);

    CassandraCustomer(final ApplicationConfig config, final byte[] id) {
        this.id = id;
        this.config = config;
    }

    CassandraCustomer(final ApplicationConfig config, final UUID uuid) {
        this.id = uuid2ByteArray(uuid);
        this.config = config;
    }

    private byte[] uuid2ByteArray(final UUID uuid) {
        final long msb = uuid.getMostSignificantBits();
        final long lsb = uuid.getLeastSignificantBits();
        final byte[] buffer = new byte[16];
        for (int i = 0; i < 8; i++) {
            buffer[i] = (byte) (msb >>> 8 * (7 - i));
        }
        for (int i = 8; i < 16; i++) {
            buffer[i] = (byte) (lsb >>> 8 * (7 - i));
        }
        return buffer;
    }

    @Override
    public byte[] primaryKey() {
        return this.id;
    }

    public CompletableFuture<DatabaseResultField<String>> firstName() {
        return this.findOne(new SingleColumnQuery<>(CassandraCustomer.TABLE_NAME, QUERY, Fields.FIRST_NAME, Fields.ID, this.primaryKey()), () ->
                this.objectValid);
    }

    public CompletableFuture<DatabaseResultField<Long>> number() {
        return this.findOne(new SingleColumnQuery<>(CassandraCustomer.TABLE_NAME, QUERY, Fields.NUMBER, Fields.ID, this.primaryKey()), () ->
                this.objectValid);
    }

    @Override
    public CompletableFuture<CassandraCustomer> save(final ColumnList columnValuesToSet) {
        final SingleColumnUpdateQuery<byte[]> query =
                new SingleColumnUpdateQuery<>(CassandraCustomer.TABLE_NAME, "UPDATE customer SET %s WHERE %%s = ?", Fields.ID, id, columnValuesToSet);
        final CompletableFuture<Integer> updated = this.update(query, () -> this.objectValid);
        this.objectValid.set(false);
        return updated.thenApply(rowsAffected -> new CassandraCustomer(config, id));
    }

    @Override
    public ApplicationConfig config() {
        return this.config;
    }

    public static final class Fields {
        public static final DatabaseField<String> FIRST_NAME =
                new DatabaseField<>("customer_first_name", String.class, Types.VARCHAR);
        static final DatabaseField<byte[]> ID = new DatabaseField<>("id", byte[].class, Types.VARBINARY);
        static final DatabaseField<Long> NUMBER =
                new DatabaseField<>("customer_number", Long.class, Types.BIGINT);
    }
}
