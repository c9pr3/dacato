package co.ecso.jdao.helpers;

import co.ecso.jdao.config.ApplicationConfig;
import co.ecso.jdao.database.DatabaseTable;
import co.ecso.jdao.database.query.DatabaseField;
import co.ecso.jdao.database.query.InsertQuery;
import co.ecso.jdao.database.query.MultiColumnQuery;
import co.ecso.jdao.database.query.SingleColumnQuery;

import java.util.HashMap;
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
@SuppressWarnings("WeakerAccess")
public final class Customers implements DatabaseTable<Long, Customer> {

    private final ApplicationConfig config;

    public Customers(final ApplicationConfig config) {
        this.config = config;
    }

    @Override
    public CompletableFuture<Customer> findOne(final Long id) {
        return this.findOne(new SingleColumnQuery<>("SELECT %s FROM customer WHERE %s = ?", Customer.Fields.ID,
                Customer.Fields.ID, id)).thenApply(foundId -> new Customer(config, foundId.value()));
    }

    @Override
    public CompletableFuture<List<Customer>> findAll() {
        return this.findMany(new SingleColumnQuery<>("SELECT %s FROM customer", Customer.Fields.ID))
                .thenApply(list -> list.stream().map(foundId -> new Customer(config, foundId.value()))
                        .collect(Collectors.toList()));
    }

    public CompletableFuture<Customer> findOneByFirstName(final String firstName) {
        final SingleColumnQuery<Long, String> query = new SingleColumnQuery<>("SELECT %s FROM customer WHERE %s = ? " +
                "LIMIT 1", Customer.Fields.ID, Customer.Fields.FIRST_NAME, firstName);
        return this.findOne(query).thenApply(foundId -> new Customer(config, foundId.value()));
    }

    public CompletableFuture<List<Customer>> findAllByFirstName(final String firstName) {
        final SingleColumnQuery<Long, String> query = new SingleColumnQuery<>("SELECT %s FROM customer WHERE %s = ?",
                Customer.Fields.ID,
                Customer.Fields.FIRST_NAME, firstName);
        return this.findMany(query).thenApply(list ->
                list.stream().map(l -> new Customer(config, l.value())).collect(Collectors.toList()));
    }

    public CompletableFuture<Customer> findOneByFirstNameAndLastName(final String firstName, final String lastName) {
        final Map<DatabaseField<?>, Object> map = new HashMap<>();
        map.put(Customer.Fields.FIRST_NAME, "foo1");
        map.put(Customer.Fields.LAST_NAME, "foo1");
        return findOne(new MultiColumnQuery<>("SELECT %s FROM customer WHERE %s = ? AND %s = ?",
                Customer.Fields.ID, () -> map)).thenApply(id -> new Customer(config, id.value()));
    }

    public CompletableFuture<Boolean> removeAll() {
        return this.truncate("TRUNCATE TABLE customer");
    }

    @Override
    public ApplicationConfig config() {
        return config;
    }

    public CompletableFuture<Customer> create(final String firstName, final String lastName, final long number) {
        final InsertQuery<Long> query = new InsertQuery<>(
                "INSERT INTO customer (%s, %s, %s, %s) VALUES (null, ?, ?, ?)", Customer.Fields.ID);
        query.add(Customer.Fields.FIRST_NAME, firstName);
        query.add(Customer.Fields.LAST_NAME, lastName);
        query.add(Customer.Fields.NUMBER, number);
        return this.add(query).thenApply(newId -> new Customer(config, newId.value()));
    }

}
