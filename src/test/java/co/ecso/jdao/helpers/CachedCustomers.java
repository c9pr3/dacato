package co.ecso.jdao.helpers;

import co.ecso.jdao.TestApplicationCache;
import co.ecso.jdao.config.ApplicationConfig;
import co.ecso.jdao.database.CachedDatabaseTable;
import co.ecso.jdao.database.cache.Cache;
import co.ecso.jdao.database.internals.Truncater;
import co.ecso.jdao.database.query.DatabaseResultField;
import co.ecso.jdao.database.query.InsertQuery;
import co.ecso.jdao.database.query.Query;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * CachedCustomers.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 17.09.16
 */
public final class CachedCustomers implements CachedDatabaseTable<Long, Customer> {

    private final ApplicationConfig config;
    private static final Cache<Query<Long>, CompletableFuture<DatabaseResultField<Long>>> CACHE =
            new TestApplicationCache<>();

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

    @Override
    public ApplicationConfig config() {
        return config;
    }

    @Override
    public CompletableFuture<Customer> findOne(final Long id) {
        return null;
    }

    @Override
    public CompletableFuture<List<Customer>> findAll() {
        return null;
    }

    @Override
    public Truncater truncater() {
        return () -> config;
    }

    @Override
    public <K, V> Cache<K, V> cache() {
        return (Cache<K, V>) CACHE;
    }
}
