package co.ecso.dacato.cassandra;

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
final class CassandraProducts implements DatabaseTable<Integer, CassandraProduct> {

    private final ApplicationConfig config;

    CassandraProducts(final ApplicationConfig config) {
        this.config = config;
    }

    @Override
    public CompletableFuture<CassandraProduct> findOne(final Integer id) {
        return findOne(new SingleColumnQuery<>(CassandraProduct.TABLE_NAME,
                "SELECT %s FROM " + CassandraProduct.TABLE_NAME + " WHERE %s = ?",
                CassandraProduct.Fields.ID, CassandraProduct.Fields.ID, id
        )).thenApply(id1 -> new CassandraProduct(config, id1.resultValue()));
    }

    public CompletableFuture<CassandraProduct> create(final String ean) {
        final InsertQuery<Integer> query = new InsertQuery<>(CassandraProduct.TABLE_NAME, "INSERT INTO " + CassandraProduct.TABLE_NAME +
                " (%s) VALUES (??)", CassandraProduct.Fields.ID);
        query.add(CassandraProduct.Fields.EAN, ean);
        return add(query).thenApply(id -> new CassandraProduct(config, id.resultValue()));
    }

    @Override
    public CompletableFuture<List<CassandraProduct>> findAll() {
        return this.findAll(CassandraProduct.TABLE_NAME, new SingleColumnQuery<>(CassandraProduct.TABLE_NAME, "SELECT %s FROM " + CassandraProduct.TABLE_NAME,
                CassandraProduct.Fields.ID))
                .thenApply(list -> list.stream().map(foundId -> new CassandraProduct(config, foundId.resultValue()))
                        .collect(Collectors.toList()));
    }

    public CompletableFuture<Boolean> removeAll() {
        return truncate(new TruncateQuery<>(CassandraProduct.TABLE_NAME, String.format("TRUNCATE TABLE %s", CassandraProduct.TABLE_NAME)));
    }

    @Override
    public ApplicationConfig config() {
        return config;
    }

    public CompletableFuture<CassandraProduct> create(final String ean, final Transaction transaction) {
        final InsertQuery<Integer> query = new InsertQuery<>(CassandraProduct.TABLE_NAME, "INSERT INTO " + CassandraProduct.TABLE_NAME +
                " (%s) VALUES (?)", CassandraProduct.Fields.ID);
        query.add(CassandraProduct.Fields.EAN, ean);
        return add(query, transaction).thenApply(id -> new CassandraProduct(config, id.resultValue()));
    }
}
