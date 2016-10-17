package co.ecso.dacato.database.statement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * StatementPreparer.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 11.10.16
 */
public interface StatementPreparer {

    /**
     * Prepare a statement.
     *
     * @param finalQuery Query to prepare.
     * @param connection Connection.
     * @return PreparedStatement.
     * @throws SQLException if SQL fails.
     */
    default PreparedStatement prepareStatement(final String finalQuery, final Connection connection, int option)
            throws SQLException {
        if (option > 0) {
            return connection.prepareStatement(finalQuery, option);
        } else {
            return connection.prepareStatement(finalQuery);
        }
    }
}
