package co.ecso.dacato.helpers;

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
public class Products implements DatabaseTable<Integer, Product> {

    private final ApplicationConfig config;

    public Products(final ApplicationConfig config) {
        this.config = config;
    }

    @Override
    public final CompletableFuture<Product> findOne(final Integer id) {
        return findOne(new SingleColumnQuery<>(Product.TABLE_NAME,
                "SELECT %s FROM " + Product.TABLE_NAME + " WHERE %s = ?",
                Product.Fields.ID, Product.Fields.ID, id
        )).thenApply(id1 -> new Product(config, id1.resultValue()));
    }

    public final CompletableFuture<Product> create(final String ean) {
        final InsertQuery<Integer> query = new InsertQuery<>(Product.TABLE_NAME, "INSERT INTO " + Product.TABLE_NAME + " (" +
                "%s) VALUES (?)", Product.Fields.ID);
        query.add(Product.Fields.EAN, ean);
        return add(query).thenApply(id -> new Product(config, id.resultValue()));
    }

    @Override
    public final CompletableFuture<List<Product>> findAll() {
        return this.findAll(Product.TABLE_NAME, new SingleColumnQuery<>(Product.TABLE_NAME, "SELECT %s FROM " + Product.TABLE_NAME, Product.Fields.ID))
                .thenApply(list -> list.stream().map(foundId -> new Product(config, foundId.resultValue()))
                        .collect(Collectors.toList()));
    }

    public final CompletableFuture<Boolean> removeAll() {
        return truncate(new TruncateQuery<>(Product.TABLE_NAME, String.format("TRUNCATE TABLE %s", Product.TABLE_NAME)));
    }

    @Override
    public final ApplicationConfig config() {
        return config;
    }

    public final CompletableFuture<Product> create(final String ean, final Transaction transaction) {
        final InsertQuery<Integer> query = new InsertQuery<>(Product.TABLE_NAME, "INSERT INTO " + Product.TABLE_NAME + " (" +
                "%s) VALUES (?)", Product.Fields.ID);
        query.add(Product.Fields.EAN, ean);
        return add(query, transaction).thenApply(id -> new Product(config, id.resultValue()));
    }
}
