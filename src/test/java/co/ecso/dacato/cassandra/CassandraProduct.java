package co.ecso.dacato.cassandra;

import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.database.ColumnList;
import co.ecso.dacato.database.DatabaseEntity;
import co.ecso.dacato.database.querywrapper.DatabaseField;
import co.ecso.dacato.database.querywrapper.SingleColumnUpdateQuery;

import java.sql.Types;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * CassandraProduct.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @version $Id:$
 * @since 24.09.16
 */
final class CassandraProduct implements DatabaseEntity<Integer> {
    static final String TABLE_NAME = "products";
    public static final String QUERY = String.format("SELECT %%s FROM %s WHERE products_id = ?", TABLE_NAME);
    private final Integer id;
    private final ApplicationConfig config;
    private final AtomicBoolean objectValid = new AtomicBoolean(true);

    CassandraProduct(final ApplicationConfig config, final Integer id) {
        this.config = config;
        Objects.requireNonNull(id);
        this.id = id;
    }

    @Override
    public Integer primaryKey() {
        return this.id;
    }

    @Override
    public CompletableFuture<CassandraProduct> save(final ColumnList columnList) {
        SingleColumnUpdateQuery<Integer> query = new SingleColumnUpdateQuery<>(CassandraProduct.TABLE_NAME,
                String.format("UPDATE %s SET %%s WHERE %%%%s = ?", TABLE_NAME), Fields.ID, id, columnList);
        final CompletableFuture<Integer> updated = this.update(query, () -> this.objectValid);
        this.objectValid.set(false);
        return updated.thenApply(l -> new CassandraProduct(config, id));
    }

    @Override
    public ApplicationConfig config() {
        return config;
    }

    @Override
    public String toString() {
        return "CassandraProduct{" +
                "id=" + id +
                '}';
    }

    static final class Fields {
        static final DatabaseField<Integer> ID = new DatabaseField<>("products_id", Integer.class, Types.INTEGER);
        static final DatabaseField<String> EAN = new DatabaseField<>("products_ean", String.class, Types.VARCHAR);
    }
}
