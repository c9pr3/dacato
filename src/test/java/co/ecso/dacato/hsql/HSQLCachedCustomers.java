package co.ecso.dacato.hsql;

import co.ecso.dacato.AbstractTest;
import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.database.CachedDatabaseTable;
import co.ecso.dacato.database.cache.Cache;
import co.ecso.dacato.database.querywrapper.InsertQuery;
import co.ecso.dacato.database.querywrapper.SingleColumnQuery;
import co.ecso.dacato.database.querywrapper.TruncateQuery;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * HSQLCachedCustomers.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 17.09.16
 */
final class HSQLCachedCustomers implements CachedDatabaseTable<Long, HSQLCachedCustomer> {

    private final ApplicationConfig config;

    HSQLCachedCustomers(final ApplicationConfig config) {
        this.config = config;
    }

    public CompletableFuture<HSQLCachedCustomer> create(final String firstName, final long number) {
        final InsertQuery<Long> query = new InsertQuery<>(HSQLCachedCustomer.TABLE_NAME,
                "INSERT INTO customer (%s, %s) VALUES (?, ?)", HSQLCachedCustomer.Fields.ID);
        query.add(HSQLCachedCustomer.Fields.FIRST_NAME, firstName);
        query.add(HSQLCachedCustomer.Fields.NUMBER, number);
        return this.add(query).thenApply(newId -> new HSQLCachedCustomer(config, newId.resultValue()));
    }

    public CompletableFuture<Boolean> removeAll() {
        return truncate(new TruncateQuery<>(HSQLCachedCustomer.TABLE_NAME, "TRUNCATE TABLE customer"));
    }

    @Override
    public CompletableFuture<HSQLCachedCustomer> findOne(final Long primaryKey) {
        return this.findOne(new SingleColumnQuery<>(HSQLCachedCustomer.TABLE_NAME, "SELECT %s FROM customer WHERE %s = ?",
                HSQLCachedCustomer.Fields.ID, HSQLCachedCustomer.Fields.ID, primaryKey)).thenApply(foundId ->
                new HSQLCachedCustomer(config, foundId.resultValue()));
    }

    @Override
    public CompletableFuture<List<HSQLCachedCustomer>> findAll() {
        return this.findAll(HSQLCachedCustomer.TABLE_NAME, new SingleColumnQuery<>(HSQLCachedCustomer.TABLE_NAME, "SELECT %s FROM customer", HSQLCachedCustomer.Fields.ID))
                .thenApply(list -> list.stream().map(foundId -> new HSQLCachedCustomer(config, foundId.resultValue()))
                        .collect(Collectors.toList()));
    }

    @Override
    public ApplicationConfig config() {
        return config;
    }

    @Override
    public Cache cache() {
        return AbstractTest.CACHE;
    }
}
