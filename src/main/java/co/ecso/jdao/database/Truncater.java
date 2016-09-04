package co.ecso.jdao.database;

import co.ecso.jdao.config.ConfigGetter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.CompletableFuture;

/**
 * Truncater.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 28.08.16
 */
interface Truncater extends ConfigGetter {

    default CompletableFuture<Boolean> truncate(final String query) {
        final CompletableFuture<Boolean> retValFuture = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try (final Connection c = config().getConnectionPool().getConnection()) {
                try (final PreparedStatement stmt = c.prepareStatement(query)) {
                    retValFuture.complete(stmt.execute());
                }
            } catch (final Exception e) {
                retValFuture.completeExceptionally(e);
            }
        }, config().getThreadPool());
        return retValFuture;
    }
}
