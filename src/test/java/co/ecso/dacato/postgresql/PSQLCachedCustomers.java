package co.ecso.dacato.postgresql;

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
 * PSQLCachedCustomers.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @version $Id:$
 * @since 17.09.16
 */
final class PSQLCachedCustomers implements CachedDatabaseTable<Long, PSQLCachedCustomer> {

    private final ApplicationConfig config;

    PSQLCachedCustomers(final ApplicationConfig config) {
        this.config = config;
    }

    public CompletableFuture<PSQLCachedCustomer> create(final String firstName, final long number) {
        final InsertQuery<Long> query = new InsertQuery<>(PSQLCachedCustomer.TABLE_NAME,
                "INSERT INTO customer (%s, %s) VALUES (?, ?)", PSQLCachedCustomer.Fields.ID);
        query.add(PSQLCachedCustomer.Fields.FIRST_NAME, firstName);
        query.add(PSQLCachedCustomer.Fields.NUMBER, number);
        return this.add(query).thenApply(newId -> new PSQLCachedCustomer(config, newId.resultValue()));
    }

    public CompletableFuture<Boolean> removeAll() {
        return truncate(new TruncateQuery<>(PSQLCachedCustomer.TABLE_NAME, "TRUNCATE TABLE customer"));
    }

    @Override
    public CompletableFuture<PSQLCachedCustomer> findOne(final Long primaryKey) {
        return this.findOne(new SingleColumnQuery<>(PSQLCachedCustomer.TABLE_NAME, "SELECT %s FROM customer WHERE %s = ?",
                PSQLCachedCustomer.Fields.ID, PSQLCachedCustomer.Fields.ID, primaryKey)).thenApply(foundId ->
                new PSQLCachedCustomer(config, foundId.resultValue()));
    }

    @Override
    public CompletableFuture<List<PSQLCachedCustomer>> findAll() {
        return this.findAll(PSQLCachedCustomer.TABLE_NAME, new SingleColumnQuery<>(PSQLCachedCustomer.TABLE_NAME, "SELECT %s FROM customer", PSQLCachedCustomer.Fields.ID))
                .thenApply(list -> list.stream().map(foundId -> new PSQLCachedCustomer(config, foundId.resultValue()))
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
