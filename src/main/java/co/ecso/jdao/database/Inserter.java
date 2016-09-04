package co.ecso.jdao.database;

import co.ecso.jdao.config.ConfigGetter;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Inserter.
 *
 * @param <T> ReturnValue of the insert. Usually Long
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 28.08.16
 */
interface Inserter<T> extends ConfigGetter, StatementFiller {

    default CompletableFuture<T> insert(final String query, final Map<DatabaseField<?>, ?> values) {
        final CompletableFuture<T> retValFuture = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try {
                try (final Connection c = config().getConnectionPool().getConnection()) {
                    try (final PreparedStatement stmt = c.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                        fillStatement(new ArrayList<>(values.keySet()), new ArrayList<>(values.values()), stmt);
                        retValFuture.complete(getResult(query, stmt));
                    }
                }
            } catch (final SQLException e) {
                retValFuture.completeExceptionally(e);
            }
        }, config().getThreadPool());
        return retValFuture;
    }

    default T getResult(final String query, final PreparedStatement stmt) throws SQLException {
        stmt.executeUpdate();
        try (final ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (!generatedKeys.next()) {
                throw new SQLException(String.format("Query %s failed, resultset empty", query));
            }
            //noinspection unchecked
            return (T) generatedKeys.getObject(1);
        }
    }

}
