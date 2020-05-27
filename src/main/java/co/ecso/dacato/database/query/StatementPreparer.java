package co.ecso.dacato.database.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * StatementPreparer.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @since 11.10.16
 */
public interface StatementPreparer {

    /**
     * Prepare a statement.
     *
     * @param finalQuery Query to prepare.
     * @param c Connection.
     * @return PreparedStatement.
     * @throws SQLException if SQL fails.
     */
    default PreparedStatement prepareStatement(final String finalQuery, final Connection c, final int option)
            throws SQLException {
        synchronized (c) {
            if (option > 0) {
                return c.prepareStatement(finalQuery, option);
            } else {
                return c.prepareStatement(finalQuery);
            }
        }
    }
}
