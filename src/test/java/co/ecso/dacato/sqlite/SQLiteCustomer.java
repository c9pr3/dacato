package co.ecso.dacato.sqlite;

import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.helpers.Customer;

import java.sql.ResultSet;

/**
 * SQLiteCustomer.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 11.10.16
 */
final class SQLiteCustomer extends Customer {

    SQLiteCustomer(final ApplicationConfig config, final Long id) {
        super(config, id);
    }

    @Override
    public int statementOptions() {
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }
}
