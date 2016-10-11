package co.ecso.dacato.postgresql;

import co.ecso.dacato.database.query.InsertQuery;
import co.ecso.dacato.helpers.Customer;
import co.ecso.dacato.helpers.Customers;

import java.util.concurrent.CompletableFuture;

/**
 * PSQLCustomers.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 11.10.16
 */
final class PSQLCustomers extends Customers {

    PSQLCustomers(final PSQLTestApplicationConfig psqlTestApplicationConfig) {
        super(psqlTestApplicationConfig);
    }

    @Override
    public CompletableFuture<Customer> create(final String firstName, final Long number) {
        final InsertQuery<Long> query = new InsertQuery<>(
                "INSERT INTO customer (%s, %s) VALUES (?, ?)", Customer.Fields.ID);
        query.add(Customer.Fields.FIRST_NAME, firstName);
        query.add(Customer.Fields.NUMBER, number);
        return this.add(query).thenApply(newId ->
                new Customer(this.config(), newId.resultValue()));
    }

}
