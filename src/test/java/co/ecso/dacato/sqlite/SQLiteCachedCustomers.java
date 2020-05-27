package co.ecso.dacato.sqlite;

import co.ecso.dacato.AbstractTest;
import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.database.CachedDatabaseTable;
import co.ecso.dacato.database.cache.Cache;
import co.ecso.dacato.database.querywrapper.InsertQuery;
import co.ecso.dacato.database.querywrapper.SingleColumnQuery;
import co.ecso.dacato.database.querywrapper.TruncateQuery;

import java.sql.ResultSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * SQLiteCachedCustomers.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @version $Id:$
 * @since 17.09.16
 */
class SQLiteCachedCustomers implements CachedDatabaseTable<Integer, SQLiteCachedCustomer> {

    private final ApplicationConfig config;

    SQLiteCachedCustomers(final ApplicationConfig config) {
        this.config = config;
    }

    public CompletableFuture<SQLiteCachedCustomer> create(final String firstName, final Integer number) {
        final InsertQuery<Integer> query = new InsertQuery<>(SQLiteCachedCustomer.TABLE_NAME,
                "INSERT INTO customer (%s, %s) VALUES (?, ?)", SQLiteCachedCustomer.Fields.ID);
        query.add(SQLiteCachedCustomer.Fields.FIRST_NAME, firstName);
        query.add(SQLiteCachedCustomer.Fields.NUMBER, number);
        return this.add(query).thenApply(newId -> new SQLiteCachedCustomer(config, newId.resultValue()));
    }

    public CompletableFuture<Boolean> removeAll() {
        return truncate(new TruncateQuery<>(SQLiteCachedCustomer.TABLE_NAME, "DELETE FROM customer"));
    }

    @Override
    public CompletableFuture<SQLiteCachedCustomer> findOne(final Integer primaryKey) {
        return this.findOne(new SingleColumnQuery<>(SQLiteCachedCustomer.TABLE_NAME, "SELECT %s FROM customer WHERE %s = ?",
                SQLiteCachedCustomer.Fields.ID, SQLiteCachedCustomer.Fields.ID, primaryKey)).thenApply(foundId ->
                new SQLiteCachedCustomer(config, foundId.resultValue()));
    }

    @Override
    public CompletableFuture<List<SQLiteCachedCustomer>> findAll() {
        return this.findAll(SQLiteCachedCustomer.TABLE_NAME, new SingleColumnQuery<>(SQLiteCachedCustomer.TABLE_NAME, "SELECT %s FROM customer", SQLiteCachedCustomer.Fields.ID))
                .thenApply(list -> list.stream().map(foundId -> new SQLiteCachedCustomer(config, foundId.resultValue()))
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

    @Override
    public int statementOptions() {
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }
}
