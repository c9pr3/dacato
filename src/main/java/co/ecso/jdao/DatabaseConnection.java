package co.ecso.jdao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * DatabaseConnection.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 16.03.16
 */
public interface DatabaseConnection {

    Connection pooledConnection() throws SQLException;
}
