package co.ecso.dacato.postgresql;

import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.database.DatabaseTable;
import co.ecso.dacato.database.querywrapper.InsertQuery;
import co.ecso.dacato.database.querywrapper.SingleColumnQuery;
import co.ecso.dacato.database.querywrapper.TruncateQuery;
import co.ecso.dacato.database.transaction.Transaction;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * CassandraProducts.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 24.09.16
 */
public final class PSQLProducts implements DatabaseTable<Integer, PSQLProduct> {

    private final ApplicationConfig config;

    PSQLProducts(final ApplicationConfig config) {
        this.config = config;
    }

    @Override
    public CompletableFuture<PSQLProduct> findOne(final Integer id) {
        return findOne(new SingleColumnQuery<>(PSQLProduct.TABLE_NAME,
                "SELECT %s FROM " + PSQLProduct.TABLE_NAME + " WHERE %s = ?",
                PSQLProduct.Fields.ID, PSQLProduct.Fields.ID, id
        )).thenApply(id1 -> new PSQLProduct(config, id1.resultValue()));
    }

    public CompletableFuture<PSQLProduct> create(final String ean) {
        final InsertQuery<Integer> query = new InsertQuery<>(PSQLProduct.TABLE_NAME, "INSERT INTO " + PSQLProduct.TABLE_NAME + " (" +
                "%s) VALUES (??)", PSQLProduct.Fields.ID);
        query.add(PSQLProduct.Fields.EAN, ean);
        return add(query).thenApply(id -> new PSQLProduct(config, id.resultValue()));
    }

    @Override
    public CompletableFuture<List<PSQLProduct>> findAll() {
        return this.findAll(PSQLProduct.TABLE_NAME, new SingleColumnQuery<>(PSQLProduct.TABLE_NAME,
                "SELECT %s FROM " + PSQLProduct.TABLE_NAME, PSQLProduct.Fields.ID))
                .thenApply(list -> list.stream().map(foundId -> new PSQLProduct(config, foundId.resultValue()))
                        .collect(Collectors.toList()));
    }

    public CompletableFuture<Boolean> removeAll() {
        return truncate(new TruncateQuery<>(PSQLProduct.TABLE_NAME, String.format("TRUNCATE TABLE %s", PSQLProduct.TABLE_NAME)));
    }

    @Override
    public ApplicationConfig config() {
        return config;
    }

    public CompletableFuture<PSQLProduct> create(final String ean, final Transaction transaction) {
        final InsertQuery<Integer> query = new InsertQuery<>(PSQLProduct.TABLE_NAME,
                "INSERT INTO products (%s) VALUES (?)", PSQLProduct.Fields.ID);
        query.add(PSQLProduct.Fields.EAN, ean);
        return add(query, transaction).thenApply(id -> new PSQLProduct(config, id.resultValue()));
    }
}
