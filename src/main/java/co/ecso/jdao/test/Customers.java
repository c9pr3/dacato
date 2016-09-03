package co.ecso.jdao.test;

import co.ecso.jdao.*;

import java.util.*;
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
public final class Customers {

    private final ApplicationConfig config;

    public Customers(final ApplicationConfig config) {
        Objects.requireNonNull(config);
        this.config = config;
    }

    CompletableFuture<Boolean> removeAll() {
        return ((Truncater) () -> config).truncate("TRUNCATE TABLE customer");
    }

    CompletableFuture<Customer> findOne(final CompletableFuture<Long> id) {
        return ((SingleColumnFinder<Long>) () -> config).find(
                new SingleFindQuery<>("SELECT %s FROM customer WHERE id = ?", Customer.Fields.ID,
                        new ColumnList().get(Customer.Fields.ID, id)))
                .thenApply(id1 -> new Customer(config, id1));
    }

    public CompletableFuture<Customer> add(final String customerFirstName, final String customerLastName,
                                           final long customerNumber) {
        final Map<DatabaseField<?>, Object> map = new LinkedHashMap<>();
        map.put(Customer.Fields.FIRST_NAME, customerFirstName);
        map.put(Customer.Fields.LAST_NAME, customerLastName);
        map.put(Customer.Fields.NUMBER, customerNumber);
        return ((Inserter<Long>) () -> config)
                .insert("INSERT INTO customer VALUES (null, ?, ?, ?)", map)
                .thenApply(id -> new Customer(config, id));
    }

    public CompletableFuture<List<Customer>> findAll() {
        return ((SingleColumnFinder<Long>) () -> config)
                .find(new ListFindQuery<>("SELECT %s FROM customer", Customer.Fields.ID))
                .thenApply(idList -> idList.stream().map(id1 -> new Customer(config, id1))
                        .collect(Collectors.toList()));
    }

    public CompletableFuture<List<List<?>>> findIdAndFirstNameByID(final CompletableFuture<Long> id,
                                                             final CompletableFuture<String> firstName) {

        final List<DatabaseField<?>> columnsToSelect = new LinkedList<>();
        columnsToSelect.add(Customer.Fields.ID);
        columnsToSelect.add(Customer.Fields.FIRST_NAME);
        columnsToSelect.add(Customer.Fields.LAST_NAME);

        final Map<DatabaseField<?>, CompletableFuture<?>> columnsWhere = new HashMap<>();
        columnsWhere.put(Customer.Fields.ID, id);
        columnsWhere.put(Customer.Fields.FIRST_NAME, firstName);

        return ((MultipleColumnFinder) () -> config).find(
                new MultipleFindQuery("SELECT %s, %s, %s FROM customer WHERE %s = ? AND %s = ?",
                        columnsToSelect, columnsWhere)
        );
    }
}
