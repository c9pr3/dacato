package co.ecso.jdao.helpers;

import co.ecso.jdao.AbstractTest;
import co.ecso.jdao.config.ApplicationConfig;
import co.ecso.jdao.database.CachedDatabaseTable;
import co.ecso.jdao.database.cache.Cache;
import co.ecso.jdao.database.internals.Truncater;
import co.ecso.jdao.database.query.InsertQuery;
import co.ecso.jdao.database.query.SingleColumnQuery;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * CachedCustomers.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 17.09.16
 */
public final class CachedCustomers implements CachedDatabaseTable<Long, CachedCustomer> {

    private final ApplicationConfig config;

    public CachedCustomers(final ApplicationConfig config) {
        this.config = config;
    }

    public CompletableFuture<CachedCustomer> create(final String firstName, final String lastName, final long number) {
        final InsertQuery<Long> query = new InsertQuery<>(
                "INSERT INTO customer (%s, %s, %s, %s) VALUES (null, ?, ?, ?)", CachedCustomer.Fields.ID);
        query.add(CachedCustomer.Fields.FIRST_NAME, firstName);
        query.add(CachedCustomer.Fields.LAST_NAME, lastName);
        query.add(CachedCustomer.Fields.NUMBER, number);
        return this.add(query).thenApply(newId -> new CachedCustomer(config, newId.value()));
    }

    public CompletableFuture<Boolean> removeAll() {
        return truncate("TRUNCATE TABLE customer");
    }

    @Override
    public CompletableFuture<CachedCustomer> findOne(final Long id) {
        return this.findOne(new SingleColumnQuery<>("SELECT %s FROM customer WHERE %s = ?", Customer.Fields.ID,
                Customer.Fields.ID, id)).thenApply(foundId -> new CachedCustomer(config, foundId.value()));
    }

    @Override
    public CompletableFuture<List<CachedCustomer>> findAll() {
        return this.findAll(new SingleColumnQuery<>("SELECT %s FROM customer", Customer.Fields.ID))
                .thenApply(list -> list.stream().map(foundId -> new CachedCustomer(config, foundId.value()))
                        .collect(Collectors.toList()));
    }

    @Override
    public ApplicationConfig config() {
        return config;
    }

    @Override
    public <K, V> Cache<K, V> cache() {
        return (Cache<K, V>) AbstractTest.CACHE;
    }
}
