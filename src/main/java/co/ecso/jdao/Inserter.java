package co.ecso.jdao;

import java.sql.*;
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
public interface Inserter<T> extends ConfigGetter {

    default CompletableFuture<T> insert(final Query query, final Map<DatabaseField<?>, ?> values) {
        final CompletableFuture<T> returnValue = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try {
                try (Connection c = config().getConnectionPool().getConnection()) {
                    try (final PreparedStatement stmt = c.prepareStatement(query.getQuery(),
                            Statement.RETURN_GENERATED_KEYS)) {
                        int i = 1;
                        for (final DatabaseField<?> databaseField : values.keySet()) {
                            try {
                                if (values.get(databaseField) == null) {
                                    stmt.setNull(i, databaseField.sqlType());
                                } else {
                                    stmt.setObject(i, values.get(databaseField), databaseField.sqlType());
                                }
                            } catch (final SQLSyntaxErrorException e) {
                                throw new SQLException(String.format("%s. Tried %s to %d", e.getMessage(),
                                        values.get(databaseField), databaseField.sqlType()), e);
                            }
                            i++;
                        }
                        stmt.executeUpdate();
                        try (final ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                            if (!generatedKeys.next()) {
                                throw new SQLException(String.format("Query %s failed, resultset empty",
                                        query.getQuery()));
                            }
                            //noinspection unchecked
                            returnValue.complete((T) generatedKeys.getObject(1));
                        }
                    }
                }
            } catch (final Exception e) {
                returnValue.completeExceptionally(e);
            }
        }, config().getThreadPool());
        return returnValue;
    }

}
