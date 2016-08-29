package co.ecso.jdao;

import java.sql.*;
import java.util.Map;
import java.util.Objects;
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
                        fillStatement(values, stmt);
                        stmt.executeUpdate();
                        getResult(query, returnValue, stmt);
                    }
                }
            } catch (final Exception e) {
                returnValue.completeExceptionally(e);
            }
        }, config().getThreadPool());
        return returnValue;
    }

    default void getResult(final Query query, final CompletableFuture<T> retValFuture, final PreparedStatement stmt)
            throws SQLException {
        Objects.nonNull(query);
        Objects.nonNull(retValFuture);
        Objects.nonNull(stmt);
        try (final ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (!generatedKeys.next()) {
                throw new SQLException(String.format("Query %s failed, resultset empty",
                        query.getQuery()));
            }
            //noinspection unchecked
            retValFuture.complete((T) generatedKeys.getObject(1));
        }
    }

    default void fillStatement(final Map<DatabaseField<?>, ?> values, final PreparedStatement stmt)
            throws SQLException {
        int i = 1;
        for (final DatabaseField<?> databaseField : values.keySet()) {
            try {
                if (values.get(databaseField) == null) {
                    stmt.setNull(i, databaseField.sqlType());
                } else {
                    stmt.setObject(i, values.get(databaseField), databaseField.sqlType());
                }
            } catch (final SQLDataException | SQLSyntaxErrorException e) {
                throw new SQLException(String.format("Could not set %s to %d: %s",
                        values.get(databaseField), databaseField.sqlType(), e));
            }
            i++;
        }
    }

}
