package co.ecso.dacato.mysql;

import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.database.DatabaseTable;
import co.ecso.dacato.database.querywrapper.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * MySQLCustomers.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @since 15.03.16
 */
final class MySQLCustomers implements DatabaseTable<Long, MySQLCustomer> {

    private final ApplicationConfig config;

    MySQLCustomers(final ApplicationConfig config) {
        this.config = config;
    }

    @Override
    public CompletableFuture<MySQLCustomer> findOne(final Long primaryKey) {
        return this.findOne(new SingleColumnQuery<>(MySQLCustomer.TABLE_NAME, "SELECT %s FROM customer WHERE %s = ?", MySQLCustomer.Fields.ID,
                MySQLCustomer.Fields.ID, primaryKey)).thenApply(foundId ->
                new MySQLCustomer(config, foundId.resultValue()));
    }

    @Override
    public CompletableFuture<List<MySQLCustomer>> findAll() {
        return this.findAll(MySQLCustomer.TABLE_NAME, new SingleColumnQuery<>(MySQLCustomer.TABLE_NAME, "SELECT %s FROM customer", MySQLCustomer.Fields.ID))
                .thenApply(list -> list.stream().map(foundId -> new MySQLCustomer(config, foundId.resultValue()))
                        .collect(Collectors.toList()));
    }

    CompletableFuture<MySQLCustomer> findOneByFirstName(final String firstName) {
        final SingleColumnQuery<Long, String> query = new SingleColumnQuery<>(MySQLCustomer.TABLE_NAME, "SELECT %s FROM customer WHERE %s = ? " +
                "LIMIT 1", MySQLCustomer.Fields.ID, MySQLCustomer.Fields.FIRST_NAME, firstName);
        return this.findOne(query).thenApply(foundId -> new MySQLCustomer(config, foundId.resultValue()));
    }

    CompletableFuture<List<MySQLCustomer>> findAllByFirstName(final String firstName) {
        final SingleColumnQuery<Long, String> query = new SingleColumnQuery<>(MySQLCustomer.TABLE_NAME, "SELECT %s FROM customer WHERE %s = ?",
                MySQLCustomer.Fields.ID,
                MySQLCustomer.Fields.FIRST_NAME, firstName);
        return this.findMany(query).thenApply(list ->
                list.stream().map(l -> new MySQLCustomer(config, l.resultValue())).collect(Collectors.toList()));
    }

    CompletableFuture<Map<DatabaseField<?>, DatabaseResultField<?>>> findFirstNameById(final Long id) {
        final String queryStr = "SELECT %s FROM customer WHERE %s = ?";
        final List<DatabaseField<?>> columnsToSelect = new LinkedList<>();
        columnsToSelect.add(MySQLCustomer.Fields.FIRST_NAME);
        final Map<DatabaseField<?>, Object> map = new HashMap<>();
        map.put(MySQLCustomer.Fields.ID, id);
        return findOne(new MultiColumnSelectQuery<>(MySQLCustomer.TABLE_NAME, queryStr, columnsToSelect, () -> map));
    }

    CompletableFuture<List<Map<DatabaseField<?>, DatabaseResultField<?>>>> findManyFirstName() {
        final String queryStr = "SELECT %s FROM customer";
        final List<DatabaseField<?>> columnsToSelect = new LinkedList<>();
        columnsToSelect.add(MySQLCustomer.Fields.FIRST_NAME);
        final Map<DatabaseField<?>, Object> map = new HashMap<>();
        return findMany(new MultiColumnSelectQuery<>(MySQLCustomer.TABLE_NAME, queryStr, columnsToSelect, () -> map));
    }

    public CompletableFuture<Boolean> removeAll() {
        return this.truncate(new TruncateQuery<>(MySQLCustomer.TABLE_NAME, "TRUNCATE TABLE customer"));
    }

    @Override
    public ApplicationConfig config() {
        return config;
    }

    public CompletableFuture<MySQLCustomer> create(final String firstName, final Long number) {
        final InsertQuery<Long> query = new InsertQuery<>(MySQLCustomer.TABLE_NAME,
                "INSERT INTO customer (%s, %s, %s) VALUES (?, ?, ?)", MySQLCustomer.Fields.ID);
        query.add(MySQLCustomer.Fields.ID, null);
        query.add(MySQLCustomer.Fields.FIRST_NAME, firstName);
        query.add(MySQLCustomer.Fields.NUMBER, number);
        return this.add(query).thenApply(newId -> new MySQLCustomer(config, newId.resultValue()));
    }

    CompletableFuture<Integer> removeOne(final Long id) {
        Map<DatabaseField<?>, Object> map = new HashMap<>();
        map.put(MySQLCustomer.Fields.ID, id);
        return this.removeOne(new RemoveQuery<>(MySQLCustomer.TABLE_NAME, "DELETE FROM customer WHERE %s = ?", () -> map));
    }
}
