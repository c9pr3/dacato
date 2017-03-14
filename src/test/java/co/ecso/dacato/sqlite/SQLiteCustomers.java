package co.ecso.dacato.sqlite;

import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.database.DatabaseTable;
import co.ecso.dacato.database.querywrapper.*;
import co.ecso.dacato.database.transaction.Transaction;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * SQLiteCustomers.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 11.10.16
 */
final class SQLiteCustomers implements DatabaseTable<Integer, SQLiteCustomer> {

    private final ApplicationConfig config;

    SQLiteCustomers(final ApplicationConfig config) {
        this.config = config;
    }

    @Override
    public int statementOptions() {
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

    @Override
    public ApplicationConfig config() {
        return config;
    }

    @Override
    public CompletableFuture<SQLiteCustomer> findOne(final Integer primaryKey) {
        return this.findOne(new SingleColumnQuery<>(SQLiteCustomer.TABLE_NAME, "SELECT %s FROM customer WHERE %s = ?", SQLiteCustomer.Fields.ID,
                SQLiteCustomer.Fields.ID, primaryKey)).thenApply(foundId ->
                new SQLiteCustomer(config, foundId.resultValue()));
    }

    @Override
    public CompletableFuture<List<SQLiteCustomer>> findAll() {
        return this.findAll(SQLiteCustomer.TABLE_NAME, new SingleColumnQuery<>(SQLiteCustomer.TABLE_NAME, "SELECT %s FROM customer", SQLiteCustomer.Fields.ID))
                .thenApply(list -> list.stream().map(foundId -> new SQLiteCustomer(config, foundId.resultValue()))
                        .collect(Collectors.toList()));
    }

    CompletableFuture<SQLiteCustomer> findOneByFirstName(final String firstName) {
        final SingleColumnQuery<Integer, String> query = new SingleColumnQuery<>(SQLiteCustomer.TABLE_NAME, "SELECT %s FROM " +
                "customer WHERE %s = ? LIMIT 1", SQLiteCustomer.Fields.ID, SQLiteCustomer.Fields.FIRST_NAME, firstName);
        return this.findOne(query).thenApply(foundId -> new SQLiteCustomer(config, foundId.resultValue()));
    }

    CompletableFuture<List<SQLiteCustomer>> findAllByFirstName(final String firstName) {
        final SingleColumnQuery<Integer, String> query = new SingleColumnQuery<>(SQLiteCustomer.TABLE_NAME, "SELECT %s FROM customer WHERE %s = ?",
                SQLiteCustomer.Fields.ID,
                SQLiteCustomer.Fields.FIRST_NAME, firstName);
        return this.findMany(query).thenApply(list ->
                list.stream().map(l -> new SQLiteCustomer(config, l.resultValue())).collect(Collectors.toList()));
    }

    CompletableFuture<Map<DatabaseField<?>, DatabaseResultField<?>>> findFirstNameById(final Integer id) {
        final String queryStr = "SELECT %s FROM customer WHERE %s = ?";
        final List<DatabaseField<?>> columnsToSelect = new LinkedList<>();
        columnsToSelect.add(SQLiteCustomer.Fields.FIRST_NAME);
        final Map<DatabaseField<?>, Object> map = new HashMap<>();
        map.put(SQLiteCustomer.Fields.ID, id);
        return findOne(new MultiColumnSelectQuery<>(SQLiteCustomer.TABLE_NAME, queryStr, columnsToSelect, () -> map));
    }

    CompletableFuture<List<Map<DatabaseField<?>, DatabaseResultField<?>>>> findManyFirstName() {
        final String queryStr = "SELECT %s FROM customer";
        final List<DatabaseField<?>> columnsToSelect = new LinkedList<>();
        columnsToSelect.add(SQLiteCustomer.Fields.FIRST_NAME);
        final Map<DatabaseField<?>, Object> map = new HashMap<>();
        return findMany(new MultiColumnSelectQuery<>(SQLiteCustomer.TABLE_NAME, queryStr, columnsToSelect, () -> map));
    }

    public CompletableFuture<Boolean> removeAll() {
        return this.truncate(new TruncateQuery<>(SQLiteCustomer.TABLE_NAME, "DELETE FROM customer"));
    }

    public CompletableFuture<SQLiteCustomer> create(final String firstName, final Integer number) {
        final InsertQuery<Integer> query = new InsertQuery<>(SQLiteCustomer.TABLE_NAME,
                "INSERT INTO customer (%s, %s) VALUES (?, ?)", SQLiteCustomer.Fields.ID);
        query.add(SQLiteCustomer.Fields.FIRST_NAME, firstName);
        query.add(SQLiteCustomer.Fields.NUMBER, number);
        return this.add(query).thenApply(newId -> {
            if (newId == null) {
                throw new RuntimeException("NEWID NULL");
            }
            return new SQLiteCustomer(config, newId.resultValue());
        });
    }

    public CompletableFuture<Integer> removeOne(final Integer id) {
        Map<DatabaseField<?>, Object> map = new HashMap<>();
        map.put(SQLiteCustomer.Fields.ID, id);
        return this.removeOne(new RemoveQuery<>(SQLiteCustomer.TABLE_NAME, "DELETE FROM customer WHERE %s = ?", () -> map));
    }

    public CompletableFuture<Integer> removeOne(final Integer id, final Transaction transaction) {
        Map<DatabaseField<?>, Object> map = new HashMap<>();
        map.put(SQLiteCustomer.Fields.ID, id);
        return this.removeOne(new RemoveQuery<>(SQLiteCustomer.TABLE_NAME, "DELETE FROM customer WHERE %s = ?", () -> map), transaction);
    }

    public CompletableFuture<SQLiteCustomer> create(final String firstName, final Integer number,
                                                    final Transaction transaction) {
        final InsertQuery<Integer> query = new InsertQuery<>(SQLiteCustomer.TABLE_NAME,
                "INSERT INTO customer (%s, %s) VALUES (?, ?)", SQLiteCustomer.Fields.ID);
        query.add(SQLiteCustomer.Fields.FIRST_NAME, firstName);
        query.add(SQLiteCustomer.Fields.NUMBER, number);
        return this.add(query, transaction).thenApply(newId -> {
            if (newId == null) {
                throw new RuntimeException("NEWID NULL");
            }
            return new SQLiteCustomer(config, newId.resultValue());
        });
    }

}
