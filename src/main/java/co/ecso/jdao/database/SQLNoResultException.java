package co.ecso.jdao.database;

import java.sql.SQLException;

/**
 * SQLNoResultException.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 16.09.16
 */
public final class SQLNoResultException extends SQLException {
    public SQLNoResultException(final String message) {
        super(message);
    }

}
