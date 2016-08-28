package co.ecso.jdao;

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
public interface Truncater extends ConfigGetter {

    default CompletableFuture<Boolean> truncate(final Query query) {
        final CompletableFuture<Boolean> f = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try (Connection c = config().getConnectionPool().getConnection()) {
                try (final PreparedStatement stmt = c.prepareStatement(query.getQuery())) {
                    final boolean res = stmt.execute();
                    f.complete(res);
                }
            } catch (final Exception e) {
                f.completeExceptionally(e);
            }
        }, config().getThreadPool());
        return f;
    }
}
