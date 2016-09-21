package co.ecso.jdao.database;

import java.sql.SQLException;

/**
 * SQLNoResultException.
 * <p>
 * Because having no result is not equivalent to an SQLException.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 16.09.16
 */
public final class SQLNoResultException extends SQLException {
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -3725636639523022220L;

    /**
     * No result exception.
     *
     * @param message Exception message.
     */
    public SQLNoResultException(final String message) {
        super(message);
    }

}
