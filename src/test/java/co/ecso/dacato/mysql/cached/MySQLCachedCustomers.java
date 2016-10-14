package co.ecso.dacato.mysql.cached;

import co.ecso.dacato.AbstractTest;
import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.database.CachedDatabaseTable;
import co.ecso.dacato.database.cache.Cache;
import co.ecso.dacato.database.cache.CacheKey;
import co.ecso.dacato.database.query.InsertQuery;
import co.ecso.dacato.database.query.SingleColumnQuery;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * MySQLCachedCustomers.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 17.09.16
 */
final class MySQLCachedCustomers implements CachedDatabaseTable<Long, MySQLCachedCustomer> {

    private final ApplicationConfig config;

    MySQLCachedCustomers(final ApplicationConfig config) {
        this.config = config;
    }

    public CompletableFuture<MySQLCachedCustomer> create(final String firstName, final long number) {
        final InsertQuery<Long> query = new InsertQuery<>(
                "INSERT INTO customer (%s, %s) VALUES (?, ?)", MySQLCachedCustomer.Fields.ID);
        query.add(MySQLCachedCustomer.Fields.FIRST_NAME, firstName);
        query.add(MySQLCachedCustomer.Fields.NUMBER, number);
        return this.add(query).thenApply(newId -> new MySQLCachedCustomer(config, newId.resultValue()));
    }

    public CompletableFuture<Boolean> removeAll() {
        return truncate("TRUNCATE TABLE customer");
    }

    @Override
    public CompletableFuture<MySQLCachedCustomer> findOne(final Long primaryKey) {
        return this.findOne(new SingleColumnQuery<>("SELECT %s FROM customer WHERE %s = ?",
                MySQLCachedCustomer.Fields.ID, MySQLCachedCustomer.Fields.ID, primaryKey)).thenApply(foundId ->
                new MySQLCachedCustomer(config, foundId.resultValue()));
    }

    @Override
    public CompletableFuture<List<MySQLCachedCustomer>> findAll() {
        return this.findAll(new SingleColumnQuery<>("SELECT %s FROM customer", MySQLCachedCustomer.Fields.ID))
                .thenApply(list -> list.stream().map(foundId -> new MySQLCachedCustomer(config, foundId.resultValue()))
                        .collect(Collectors.toList()));
    }

    @Override
    public ApplicationConfig config() {
        return config;
    }

    @Override
    public Cache<CacheKey, CompletableFuture> cache() {
        return AbstractTest.CACHE;
    }
}
