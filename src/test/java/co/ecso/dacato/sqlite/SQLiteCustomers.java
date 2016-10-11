package co.ecso.dacato.sqlite;

import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.database.query.InsertQuery;
import co.ecso.dacato.helpers.Customer;
import co.ecso.dacato.helpers.Customers;

import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;

/**
 * SQLiteCustomers.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 11.10.16
 */
final class SQLiteCustomers extends Customers {

    SQLiteCustomers(final ApplicationConfig config) {
        super(config);
    }

    @Override
    public int statementOptions() {
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

    public CompletableFuture<Customer> create(final String firstName, final Long number) {
        final InsertQuery<Long> query = new InsertQuery<>(
                "INSERT INTO customer (%s, %s) VALUES (?, ?)", SQLiteCustomer.Fields.ID);
        query.add(Customer.Fields.FIRST_NAME, firstName);
        query.add(Customer.Fields.NUMBER, number);
        return this.add(query).thenApply(newId -> new SQLiteCustomer(config(),
                Long.valueOf((Integer) newId.resultValuePOJO())));
    }
}
