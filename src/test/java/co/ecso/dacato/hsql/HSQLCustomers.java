package co.ecso.dacato.hsql;

import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.database.DatabaseTable;
import co.ecso.dacato.database.query.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * HSQLCustomers.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 15.03.16
 */
final class HSQLCustomers implements DatabaseTable<Long, HSQLCustomer> {

    private final ApplicationConfig config;

    HSQLCustomers(final ApplicationConfig config) {
        this.config = config;
    }

    @Override
    public CompletableFuture<HSQLCustomer> findOne(final Long primaryKey) {
        return this.findOne(new SingleColumnQuery<>("SELECT %s FROM customer WHERE %s = ?", HSQLCustomer.Fields.ID,
                HSQLCustomer.Fields.ID, primaryKey)).thenApply(foundId ->
                new HSQLCustomer(config, foundId.resultValue()));
    }

    @Override
    public CompletableFuture<List<HSQLCustomer>> findAll() {
        return this.findAll(new SingleColumnQuery<>("SELECT %s FROM customer", HSQLCustomer.Fields.ID))
                .thenApply(list -> list.stream().map(foundId -> new HSQLCustomer(config, foundId.resultValue()))
                        .collect(Collectors.toList()));
    }

    public CompletableFuture<HSQLCustomer> findOneByFirstName(final String firstName) {
        final SingleColumnQuery<Long, String> query = new SingleColumnQuery<>("SELECT %s FROM customer WHERE %s = ? " +
                "LIMIT 1", HSQLCustomer.Fields.ID, HSQLCustomer.Fields.FIRST_NAME, firstName);
        return this.findOne(query).thenApply(foundId -> new HSQLCustomer(config, foundId.resultValue()));
    }

    public CompletableFuture<List<HSQLCustomer>> findAllByFirstName(final String firstName) {
        final SingleColumnQuery<Long, String> query = new SingleColumnQuery<>("SELECT %s FROM customer WHERE %s = ?",
                HSQLCustomer.Fields.ID,
                HSQLCustomer.Fields.FIRST_NAME, firstName);
        return this.findMany(query).thenApply(list ->
                list.stream().map(l -> new HSQLCustomer(config, l.resultValue())).collect(Collectors.toList()));
    }

    public CompletableFuture<Map<DatabaseField, DatabaseResultField>> findFirstNameById(final Long id) {
        final String queryStr = "SELECT %s FROM customer WHERE %s = ?";
        final List<DatabaseField> columnsToSelect = new LinkedList<>();
        columnsToSelect.add(HSQLCustomer.Fields.FIRST_NAME);
        final Map<DatabaseField<?>, Object> map = new HashMap<>();
        map.put(HSQLCustomer.Fields.ID, id);
        return findOne(new MultiColumnSelectQuery<>(queryStr, columnsToSelect, () -> map));
    }

    public CompletableFuture<List<Map<DatabaseField, DatabaseResultField>>> findManyFirstName() {
        final String queryStr = "SELECT %s FROM customer";
        final List<DatabaseField> columnsToSelect = new LinkedList<>();
        columnsToSelect.add(HSQLCustomer.Fields.FIRST_NAME);
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

    public CompletableFuture<HSQLCustomer> create(final String firstName, final Long number) {
        final InsertQuery<Long> query = new InsertQuery<>(
                "INSERT INTO customer (%s, %s, %s) VALUES (?, ?, ?)", HSQLCustomer.Fields.ID);
        query.add(HSQLCustomer.Fields.ID, null);
        query.add(HSQLCustomer.Fields.FIRST_NAME, firstName);
        query.add(HSQLCustomer.Fields.NUMBER, number);
        return this.add(query).thenApply(newId -> new HSQLCustomer(config, newId.resultValue()));
    }

    public CompletableFuture<Integer> removeOne(final Long id) {
        Map<DatabaseField<?>, Object> map = new HashMap<>();
        map.put(HSQLCustomer.Fields.ID, id);
        return this.removeOne(new RemoveQuery<>("DELETE FROM customer WHERE %s = ?", () -> map));
    }
}
