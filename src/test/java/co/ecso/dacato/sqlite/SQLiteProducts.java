package co.ecso.dacato.sqlite;

import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.helpers.Products;

import java.sql.ResultSet;

/**
 * SQLiteProducts.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @version $Id:$
 * @since 11.10.16
 */
final class SQLiteProducts extends Products {

    SQLiteProducts(final ApplicationConfig config) {
        super(config);
    }

    @Override
    public int statementOptions() {
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

}
