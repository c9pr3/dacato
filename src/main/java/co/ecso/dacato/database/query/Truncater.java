package co.ecso.dacato.database.query;

import co.ecso.dacato.config.ConfigGetter;
import co.ecso.dacato.database.querywrapper.TruncateQuery;
import co.ecso.dacato.database.transaction.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

/**
 * Truncater.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @since 11.09.16
 */
public interface Truncater extends ConfigGetter, StatementPreparer {

    /**
     * Truncate.
     *
     * @param truncateQuery Query.
     * @return True if truncation succeeded, false if not.
     */
    default CompletableFuture<Boolean> truncate(final TruncateQuery<?> truncateQuery) {
        final CompletableFuture<Boolean> returnValueFuture = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            Connection c = null;
            boolean result = false;
            try {
                c = connection();
                if (c == null) {
                    throw new SQLException("Could not obtain connection");
                }
                try (PreparedStatement stmt = this.prepareStatement(truncateQuery.query(), c, this.statementOptions())) {
                    result = stmt.execute();
                }
            } catch (final Exception e) {
                returnValueFuture.completeExceptionally(e);
            } finally {
                if (c != null && transaction() == null) {
                    try {
                        c.close();
                    } catch (final SQLException e) {
                        returnValueFuture.completeExceptionally(e);
                    }
                }
                if (!returnValueFuture.isCompletedExceptionally()) {
                    returnValueFuture.complete(result);
                }
            }
        }, config().threadPool());

        return returnValueFuture;
    }

    default Connection connection() throws SQLException {
        return config().databaseConnectionPool().getConnection();
    }

    default Transaction transaction() {
        return null;
    }

    int statementOptions();

}
