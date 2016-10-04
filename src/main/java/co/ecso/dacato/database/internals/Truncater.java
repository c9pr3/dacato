package co.ecso.dacato.database.internals;

import co.ecso.dacato.config.ConfigGetter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.CompletableFuture;

/**
 * Truncater.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 11.09.16
 */
public interface Truncater extends ConfigGetter {

    /**
     * Truncate.
     *
     * @param query Query.
     * @return True if truncation succeeded, false if not.
     */
    default CompletableFuture<Boolean> truncate(final String query) {
        final CompletableFuture<Boolean> retValFuture = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try (final Connection c = config().databaseConnectionPool().getConnection()) {
                try (final PreparedStatement stmt = c.prepareStatement(query)) {
                    retValFuture.complete(stmt.execute());
                }
            } catch (final Exception e) {
                retValFuture.completeExceptionally(e);
            }
        }, config().threadPool());
        return retValFuture;
    }

}
