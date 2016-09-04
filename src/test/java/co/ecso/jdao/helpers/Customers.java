package co.ecso.jdao.helpers;

import co.ecso.jdao.config.ApplicationConfig;
import co.ecso.jdao.database.*;

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
public final class Customers implements DatabaseTable<Long> {

    private final ApplicationConfig config;

    public Customers(final ApplicationConfig config) {
        this.config = config;
    }

    public CompletableFuture<Boolean> removeAll() {
        return this.truncate("TRUNCATE TABLE customer");
    }

    public CompletableFuture<Customer> findOne(final CompletableFuture<Long> id) {
        return this.find(new SingleFindQuery<>("SELECT %s FROM customer WHERE id = ?", Customer.Fields.ID,
                ColumnList.build(Customer.Fields.ID, id))).thenApply(id1 -> new Customer(config, id1));
    }

    public CompletableFuture<Customer> add(final String customerFirstName, final String customerLastName,
                                           final long customerNumber) {
        return this.insert("INSERT INTO customer VALUES (null, ?, ?, ?)",
                new ColumnList().keys(Customer.Fields.FIRST_NAME, Customer.Fields.LAST_NAME, Customer.Fields.NUMBER)
                        .values(Arrays.asList(customerFirstName, customerLastName, customerNumber)).build())
                .thenApply(id -> new Customer(config, id));
    }

    public CompletableFuture<List<Customer>> findAll() {
        return this.find(new ListFindQuery<>("SELECT %s FROM customer", Customer.Fields.ID))
                .thenApply(idList -> idList.stream().map(id1 -> new Customer(config, id1))
                        .collect(Collectors.toList()));
    }

    public CompletableFuture<List<List<?>>> findIdAndFirstNameByID(final CompletableFuture<Long> id,
                                                                   final CompletableFuture<String> firstName) {

        final List<DatabaseField<?>> columnsToSelect = new LinkedList<>();
        columnsToSelect.add(Customer.Fields.ID);
        columnsToSelect.add(Customer.Fields.FIRST_NAME);

        final Map<DatabaseField<?>, CompletableFuture<?>> columnsWhere = new HashMap<>();
        columnsWhere.put(Customer.Fields.ID, id);

        return this.find(new MultipleFindQuery("SELECT %s, %s FROM customer WHERE %s = ?",
                columnsToSelect, columnsWhere));
    }

    @Override
    public ApplicationConfig config() {
        return config;
    }
}
