package co.ecso.dacato.database.query;

import co.ecso.dacato.config.ConfigGetter;
import co.ecso.dacato.database.querywrapper.DatabaseField;
import co.ecso.dacato.database.querywrapper.DatabaseResultField;
import co.ecso.dacato.database.querywrapper.InsertQuery;
import co.ecso.dacato.database.transaction.Transaction;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Inserter.
 *
 * @param <T> Type of insert, p.e. Long -> type to return.
 * @author Christian Senkowski (cs@2scale.net)
 * @since 12.09.16
 */
public interface Inserter<T> extends ConfigGetter, StatementPreparer {

    /**
     * Statement filler.
     *
     * @return Statement filler.
     */
    default StatementFiller statementFiller() {
        return new StatementFiller() {
        };
    }

    default Transaction transaction() {
        return null;
    }

    /**
     * Add.
     *
     * @param query Query.
     * @return DatabaseResultField of type T.
     */
    default CompletableFuture<DatabaseResultField<T>> add(final InsertQuery<T> query) {
        final CompletableFuture<DatabaseResultField<T>> returnValueFuture = new CompletableFuture<>();
        final int returnGenerated = query.returnGeneratedKey() ? Statement.RETURN_GENERATED_KEYS : 0;
        final List<DatabaseField<?>> keys = new LinkedList<>();
        if (query.returnGeneratedKey() && query.query().split("%s").length - 1 > query.values().keySet().size()) {
            keys.add(query.columnToReturn());
        }
        keys.addAll(query.values().keySet());
        final String finalQuery = String.format(query.query(), keys.toArray());

        CompletableFuture.runAsync(() -> {
            Connection c = null;
            DatabaseResultField<T> result = null;
            try {
                c = connection();
                if (c == null) {
                    throw new SQLException("Could not obtain connection");
                }
                if (returnGenerated > 0) {
                    try (final PreparedStatement stmt = this.prepareStatement(finalQuery, c, returnGenerated)) {
                        statementFiller().fillStatement(finalQuery, new LinkedList<>(query.values().keySet()),
                                new LinkedList<>(query.values().values()), stmt, c);
                        result = getResult(finalQuery, query.columnToReturn(), stmt,
                                query.returnGeneratedKey(), c);
                        returnValueFuture.complete(result);
                    }
                } else {
                    try (final PreparedStatement stmt = this.prepareStatement(finalQuery, c, statementOptions())) {
                        statementFiller().fillStatement(finalQuery, new LinkedList<>(query.values().keySet()),
                                new LinkedList<>(query.values().values()), stmt, c);
                        result = getResult(finalQuery, query.columnToReturn(), stmt,
                                query.returnGeneratedKey(), c);
                    }
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

    int statementOptions();

    /**
     * Get result.
     *
     * @param finalQuery     Final query.
     * @param columnToSelect Column to select.
     * @param stmt           Statement.
     * @param c              Connection.
     * @return DatabaseResultField of type T.
     * @throws SQLException if sql fails.
     */
    default DatabaseResultField<T> getResult(final String finalQuery, final DatabaseField<T> columnToSelect,
                                             final PreparedStatement stmt, final boolean returnGeneratedKey,
                                             final Connection c) throws SQLException {
        synchronized (c) {
            try {
                if (stmt.isClosed()) {
                    throw new SQLException(String.format("Statement %s closed unexpectedly", stmt.toString()));
                }
                stmt.executeUpdate();
                if (!returnGeneratedKey) {
                    return new DatabaseResultField<>(columnToSelect, null);
                }
                return getGeneratedKeys(finalQuery, columnToSelect, stmt, c);
            } catch (final Exception e) {
                throw new SQLException(String.format("%s, query %s", e.getMessage(), finalQuery), e);
            }
        }
    }

    default DatabaseResultField<T> getGeneratedKeys(final String finalQuery, final DatabaseField<T> columnToSelect,
                                                    final PreparedStatement stmt, final Connection c)
            throws SQLException {
        synchronized (c) {
            if (stmt.isClosed()) {
                throw new SQLException(String.format("Statement %s closed unexpectedly", stmt.toString()));
            }
            try (final ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new SQLException(String.format("Query %s failed, resultset empty", finalQuery));
                }
                try {
                    if (generatedKeys.getClass().getMethod("getObject", int.class, Class.class) == null) {
                        throw new NoSuchMethodError("Driver does not support getObject with class");
                    }
                    final T value = generatedKeys.getObject(1, columnToSelect.valueClass());
                    if (value == null) {
                        throw new SQLFeatureNotSupportedException("Broken driver, gave back null for " +
                                "getObject(int, class)");
                    }
                    return new DatabaseResultField<>(columnToSelect, value);
                } catch (final SQLFeatureNotSupportedException | NoSuchMethodException ignored) {
                    //noinspection unchecked
                    final T value = (T) generatedKeys.getObject(1);
                    if (value == null) {
                        throw new SQLFeatureNotSupportedException("Broken driver, gave back null for getObject(int)");
                    }
                    return new DatabaseResultField<>(columnToSelect, value);
                }
            }
        }
    }

}
