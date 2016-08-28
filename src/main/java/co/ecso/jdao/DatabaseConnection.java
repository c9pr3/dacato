package co.ecso.jdao;

import java.sql.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * DatabaseConnection.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 16.03.16
 */
public interface DatabaseConnection {

    default CompletableFuture<Boolean> truncate(final Query query) {
        final CompletableFuture<Boolean> f = new CompletableFuture<>();
        try (final Connection c = pooledConnection()) {
            try (final PreparedStatement stmt = c.prepareStatement(query.getQuery())) {
                final boolean res = stmt.execute();
                f.complete(res);
            }
        } catch (final Exception e) {
            e.printStackTrace();
            f.completeExceptionally(e);
        }
        return f;
    }

    /**
     * Get Java SQL Connection.
     *
     * @return Connection.
     * @throws SQLException if SQL fails.
     */
    Connection pooledConnection() throws SQLException;

    default CompletableFuture<Long> insert(final Query query, final Map<DatabaseField<?>, ?> values) {
        final CompletableFuture<Long> f = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try {
                try (final Connection c = pooledConnection()) {
                    try (final PreparedStatement stmt = c.prepareStatement(query.getQuery(), Statement.RETURN_GENERATED_KEYS)) {
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
                                throw new SQLException(String.format("Query %s failed, resultset empty", query.getQuery()));
                            }
//                            int sleep = ThreadLocalRandom.current().nextInt(1000, 4000 + 1);
//                            Thread.sleep(sleep);
                            final Long rlong = generatedKeys.getLong(1);
                            f.complete(rlong);
                        }
                    }
                }
            } catch (final Exception e) {
                e.printStackTrace();
                f.completeExceptionally(e);
            }
        });
        return f;
    }

    ApplicationConfig getConfig();

    default void getResult(final Query query, final DatabaseField<?> column,
                           final CompletableFuture<Object> f, final PreparedStatement stmt) throws SQLException {
        Objects.nonNull(query);
        Objects.nonNull(column);
        Objects.nonNull(f);
        Objects.nonNull(stmt);
        try (final ResultSet rs = stmt.executeQuery()) {
            if (!rs.next()) {
                throw new SQLException(String.format("Query %s failed, resultset empty", query.getQuery()));
            }
            final Object rval = rs.getObject(column.toString(), column.valueClass());
            if (rval == null) {
                throw new SQLException(String.format("Result for %s, %s was null",
                        column.toString(), query.getQuery()));
            } else {
                if ("String".equals(column.valueClass().getSimpleName())) {
                    f.complete(rval.toString().trim());
                } else {
                    f.complete(rval);
                }
            }
        }
    }

}
