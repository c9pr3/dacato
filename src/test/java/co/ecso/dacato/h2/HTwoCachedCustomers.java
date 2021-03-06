package co.ecso.dacato.h2;

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
 * HTwoCachedCustomers.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @version $Id:$
 * @since 17.09.16
 */
final class HTwoCachedCustomers implements CachedDatabaseTable<Long, HTwoCachedCustomer> {

    private final ApplicationConfig config;

    HTwoCachedCustomers(final ApplicationConfig config) {
        this.config = config;
    }

    public CompletableFuture<HTwoCachedCustomer> create(final String firstName, final long number) {
        final InsertQuery<Long> query = new InsertQuery<>(HTwoCachedCustomer.TABLE_NAME,
                "INSERT INTO customer (%s, %s) VALUES (?, ?)", HTwoCachedCustomer.Fields.ID);
        query.add(HTwoCachedCustomer.Fields.FIRST_NAME, firstName);
        query.add(HTwoCachedCustomer.Fields.NUMBER, number);
        return this.add(query).thenApply(newId -> new HTwoCachedCustomer(config, newId.resultValue()));
    }

    public CompletableFuture<Boolean> removeAll() {
        return truncate(new TruncateQuery<>(HTwoCachedCustomer.TABLE_NAME, "TRUNCATE TABLE customer"));
    }

    @Override
    public CompletableFuture<HTwoCachedCustomer> findOne(final Long primaryKey) {
        return this.findOne(new SingleColumnQuery<>(HTwoCachedCustomer.TABLE_NAME, "SELECT %s FROM customer WHERE %s = ?",
                HTwoCachedCustomer.Fields.ID, HTwoCachedCustomer.Fields.ID, primaryKey)).thenApply(foundId ->
                new HTwoCachedCustomer(config, foundId.resultValue()));
    }

    @Override
    public CompletableFuture<List<HTwoCachedCustomer>> findAll() {
        return this.findAll(HTwoCachedCustomer.TABLE_NAME, new SingleColumnQuery<>(HTwoCachedCustomer.TABLE_NAME,
                "SELECT %s FROM customer", HTwoCachedCustomer.Fields.ID))
                .thenApply(list ->
                        list.stream().map(foundId ->
                                new HTwoCachedCustomer(config, foundId.resultValue())).collect(Collectors.toList()));
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
