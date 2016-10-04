package co.ecso.dacato.helpers;

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
 * Customers.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 15.03.16
 */
public final class Customers implements DatabaseTable<Long, Customer> {

    private final ApplicationConfig config;

    public Customers(final ApplicationConfig config) {
        this.config = config;
    }

    @Override
    public CompletableFuture<Customer> findOne(final Long primaryKey) {
        return this.findOne(new SingleColumnQuery<>("SELECT %s FROM customer WHERE %s = ?", Customer.Fields.ID,
                Customer.Fields.ID, primaryKey)).thenApply(foundId -> new Customer(config, foundId.resultValue()));
    }

    @Override
    public CompletableFuture<List<Customer>> findAll() {
        return this.findAll(new SingleColumnQuery<>("SELECT %s FROM customer", Customer.Fields.ID))
                .thenApply(list -> list.stream().map(foundId -> new Customer(config, foundId.resultValue()))
                        .collect(Collectors.toList()));
    }

    public CompletableFuture<Customer> findOneByFirstName(final String firstName) {
        final SingleColumnQuery<Long, String> query = new SingleColumnQuery<>("SELECT %s FROM customer WHERE %s = ? " +
                "LIMIT 1", Customer.Fields.ID, Customer.Fields.FIRST_NAME, firstName);
        return this.findOne(query).thenApply(foundId -> new Customer(config, foundId.resultValue()));
    }

    public CompletableFuture<List<Customer>> findAllByFirstName(final String firstName) {
        final SingleColumnQuery<Long, String> query = new SingleColumnQuery<>("SELECT %s FROM customer WHERE %s = ?",
                Customer.Fields.ID,
                Customer.Fields.FIRST_NAME, firstName);
        return this.findMany(query).thenApply(list ->
                list.stream().map(l -> new Customer(config, l.resultValue())).collect(Collectors.toList()));
    }

    public CompletableFuture<Map<DatabaseField, DatabaseResultField>> findFirstNameById(final Long id) {
        final String queryStr = "SELECT %s FROM customer WHERE %s = ?";
        final List<DatabaseField> columnsToSelect = new LinkedList<>();
        columnsToSelect.add(Customer.Fields.FIRST_NAME);
        final Map<DatabaseField<?>, Object> map = new HashMap<>();
        map.put(Customer.Fields.ID, id);
        return findOne(new MultiColumnSelectQuery<>(queryStr, columnsToSelect, () -> map));
    }

    public CompletableFuture<List<Map<DatabaseField, DatabaseResultField>>> findManyFirstName() {
        final String queryStr = "SELECT %s FROM customer";
        final List<DatabaseField> columnsToSelect = new LinkedList<>();
        columnsToSelect.add(Customer.Fields.FIRST_NAME);
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

    public CompletableFuture<Customer> create(final String firstName, final Long number) {
        final InsertQuery<Long> query = new InsertQuery<>(
                "INSERT INTO customer (%s, %s, %s) VALUES (null, ?, ?)", Customer.Fields.ID);
        query.add(Customer.Fields.FIRST_NAME, firstName);
        query.add(Customer.Fields.NUMBER, number);
        return this.add(query).thenApply(newId -> new Customer(config, newId.resultValue()));
    }

    public CompletableFuture<Integer> removeOne(final Long id) {
        Map<DatabaseField<?>, Object> map = new HashMap<>();
        map.put(Customer.Fields.ID, id);
        return this.removeOne(new RemoveQuery<>("DELETE FROM customer WHERE %s = ?", () -> map));
    }
}
