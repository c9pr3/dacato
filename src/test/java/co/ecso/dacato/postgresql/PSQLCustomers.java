package co.ecso.dacato.postgresql;

import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.database.DatabaseTable;
import co.ecso.dacato.database.querywrapper.*;
import co.ecso.dacato.database.transaction.Transaction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * PSQLCustomers.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 11.10.16
 */
final class PSQLCustomers implements DatabaseTable<Long, PSQLCustomer> {

    private final ApplicationConfig config;

    PSQLCustomers(final ApplicationConfig config) {
        this.config = config;
    }

    @Override
    public CompletableFuture<PSQLCustomer> findOne(final Long primaryKey) {
        return this.findOne(new SingleColumnQuery<>("SELECT %s FROM customer WHERE %s = ?", PSQLCustomer.Fields.ID,
                PSQLCustomer.Fields.ID, primaryKey)).thenApply(foundId ->
                new PSQLCustomer(config, foundId.resultValue()));
    }

    @Override
    public CompletableFuture<List<PSQLCustomer>> findAll() {
        return this.findAll(new SingleColumnQuery<>("SELECT %s FROM customer", PSQLCustomer.Fields.ID))
                .thenApply(list -> list.stream().map(foundId -> new PSQLCustomer(config, foundId.resultValue()))
                        .collect(Collectors.toList()));
    }

    public CompletableFuture<PSQLCustomer> findOneByFirstName(final String firstName) {
        final SingleColumnQuery<Long, String> query = new SingleColumnQuery<>("SELECT %s FROM customer WHERE %s = ? " +
                "LIMIT 1", PSQLCustomer.Fields.ID, PSQLCustomer.Fields.FIRST_NAME, firstName);
        return this.findOne(query).thenApply(foundId -> new PSQLCustomer(config, foundId.resultValue()));
    }

    public CompletableFuture<List<PSQLCustomer>> findAllByFirstName(final String firstName) {
        final SingleColumnQuery<Long, String> query = new SingleColumnQuery<>("SELECT %s FROM customer WHERE %s = ?",
                PSQLCustomer.Fields.ID,
                PSQLCustomer.Fields.FIRST_NAME, firstName);
        return this.findMany(query).thenApply(list ->
                list.stream().map(l -> new PSQLCustomer(config, l.resultValue())).collect(Collectors.toList()));
    }

    public CompletableFuture<Map<DatabaseField<?>, DatabaseResultField<?>>> findFirstNameById(final Long id) {
        final String queryStr = "SELECT %s FROM customer WHERE %s = ?";
        final List<DatabaseField<?>> columnsToSelect = new LinkedList<>();
        columnsToSelect.add(PSQLCustomer.Fields.FIRST_NAME);
        final Map<DatabaseField<?>, Object> map = new HashMap<>();
        map.put(PSQLCustomer.Fields.ID, id);
        return findOne(new MultiColumnSelectQuery<>(queryStr, columnsToSelect, () -> map));
    }

    public CompletableFuture<List<Map<DatabaseField<?>, DatabaseResultField<?>>>> findManyFirstName() {
        final String queryStr = "SELECT %s FROM customer";
        final List<DatabaseField<?>> columnsToSelect = new LinkedList<>();
        columnsToSelect.add(PSQLCustomer.Fields.FIRST_NAME);
        final Map<DatabaseField<?>, Object> map = new HashMap<>();
        return findMany(new MultiColumnSelectQuery<>(queryStr, columnsToSelect, () -> map));
    }

    public CompletableFuture<Boolean> removeAll() {
        return this.truncate("TRUNCATE TABLE customer");
    }

    @Override
    public ApplicationConfig config() {
        return config;
    }

    public CompletableFuture<Integer> removeOne(final Long id) {
        Map<DatabaseField<?>, Object> map = new HashMap<>();
        map.put(PSQLCustomer.Fields.ID, id);
        return this.removeOne(new RemoveQuery<>("DELETE FROM customer WHERE %s = ?", () -> map));
    }

    public CompletableFuture<PSQLCustomer> create(final String firstName, final Long number) {
        final InsertQuery<Long> query = new InsertQuery<>(
                "INSERT INTO customer (%s, %s) VALUES (?, ?)", PSQLCustomer.Fields.ID);
        query.add(PSQLCustomer.Fields.FIRST_NAME, firstName);
        query.add(PSQLCustomer.Fields.NUMBER, number);
        return this.add(query).thenApply(newId ->
                new PSQLCustomer(this.config(), newId.resultValue()));
    }

    public CompletableFuture<PSQLCustomer> create(final String firstName, final Long number,
                                                  final Transaction transaction) {
        final InsertQuery<Long> query = new InsertQuery<>(
                "INSERT INTO customer (%s, %s) VALUES (?, ?)", PSQLCustomer.Fields.ID);
        query.add(PSQLCustomer.Fields.FIRST_NAME, firstName);
        query.add(PSQLCustomer.Fields.NUMBER, number);
        return this.add(query, transaction).thenApply(newId ->
                new PSQLCustomer(this.config(), newId.resultValue()));
    }
}
