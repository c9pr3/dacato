package co.ecso.dacato.h2;

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
 * HTwoCustomers.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 15.03.16
 */
final class HTwoCustomers implements DatabaseTable<Long, HTwoCustomer> {

    private final ApplicationConfig config;

    HTwoCustomers(final ApplicationConfig config) {
        this.config = config;
    }

    @Override
    public CompletableFuture<HTwoCustomer> findOne(final Long primaryKey) {
        return this.findOne(new SingleColumnQuery<>("SELECT %s FROM customer WHERE %s = ?", HTwoCustomer.Fields.ID,
                HTwoCustomer.Fields.ID, primaryKey)).thenApply(foundId ->
                new HTwoCustomer(config, foundId.resultValue()));
    }

    @Override
    public CompletableFuture<List<HTwoCustomer>> findAll() {
        return this.findAll(new SingleColumnQuery<>("SELECT %s FROM customer", HTwoCustomer.Fields.ID))
                .thenApply(list -> list.stream().map(foundId -> new HTwoCustomer(config, foundId.resultValue()))
                        .collect(Collectors.toList()));
    }

    CompletableFuture<HTwoCustomer> findOneByFirstName(final String firstName) {
        final SingleColumnQuery<Long, String> query = new SingleColumnQuery<>("SELECT %s FROM customer WHERE %s = ? " +
                "LIMIT 1", HTwoCustomer.Fields.ID, HTwoCustomer.Fields.FIRST_NAME, firstName);
        return this.findOne(query).thenApply(foundId -> new HTwoCustomer(config, foundId.resultValue()));
    }

    CompletableFuture<List<HTwoCustomer>> findAllByFirstName(final String firstName) {
        final SingleColumnQuery<Long, String> query = new SingleColumnQuery<>("SELECT %s FROM customer WHERE %s = ?",
                HTwoCustomer.Fields.ID,
                HTwoCustomer.Fields.FIRST_NAME, firstName);
        return this.findMany(query).thenApply(list ->
                list.stream().map(l -> new HTwoCustomer(config, l.resultValue())).collect(Collectors.toList()));
    }

    CompletableFuture<Map<DatabaseField, DatabaseResultField>> findFirstNameById(final Long id) {
        final String queryStr = "SELECT %s FROM customer WHERE %s = ?";
        final List<DatabaseField> columnsToSelect = new LinkedList<>();
        columnsToSelect.add(HTwoCustomer.Fields.FIRST_NAME);
        final Map<DatabaseField<?>, Object> map = new HashMap<>();
        map.put(HTwoCustomer.Fields.ID, id);
        return findOne(new MultiColumnSelectQuery<>(queryStr, columnsToSelect, () -> map));
    }

    CompletableFuture<List<Map<DatabaseField, DatabaseResultField>>> findManyFirstName() {
        final String queryStr = "SELECT %s FROM customer";
        final List<DatabaseField> columnsToSelect = new LinkedList<>();
        columnsToSelect.add(HTwoCustomer.Fields.FIRST_NAME);
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

    public CompletableFuture<HTwoCustomer> create(final String firstName, final Long number) {
        final InsertQuery<Long> query = new InsertQuery<>(
                "INSERT INTO customer (%s, %s) VALUES (?, ?)", HTwoCustomer.Fields.ID);
        query.add(HTwoCustomer.Fields.FIRST_NAME, firstName);
        query.add(HTwoCustomer.Fields.NUMBER, number);
        return this.add(query).thenApply(newId -> new HTwoCustomer(config, newId.resultValue()));
    }

    CompletableFuture<Integer> removeOne(final Long id) {
        Map<DatabaseField<?>, Object> map = new HashMap<>();
        map.put(HTwoCustomer.Fields.ID, id);
        return this.removeOne(new RemoveQuery<>("DELETE FROM customer WHERE %s = ?", () -> map));
    }
}
