package co.ecso.dacato.database.query;

import co.ecso.dacato.config.ConfigGetter;
import co.ecso.dacato.database.querywrapper.DatabaseField;
import co.ecso.dacato.database.querywrapper.DatabaseResultField;
import co.ecso.dacato.database.querywrapper.InsertQuery;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Inserter.
 *
 * @param <T> Type of insert, p.e. Long -> type to return.
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
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

    /**
     * Add.
     *
     * @param query Query.
     * @return DatabaseResultField of type T.
     */
    default CompletableFuture<DatabaseResultField<T>> add(InsertQuery<T> query) {
        final CompletableFuture<DatabaseResultField<T>> returnValueFuture = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            final List<DatabaseField<?>> keys = new LinkedList<>();
            if (query.returnGeneratedKey() && query.query().split("%s").length - 1 > query.values().keySet().size()) {
                keys.add(query.columnToReturn());
            }
            keys.addAll(query.values().keySet());
            final String finalQuery = String.format(query.query(), keys.toArray());
            try (final Connection c = config().databaseConnectionPool().getConnection()) {
                final int returnGenerated = query.returnGeneratedKey() ? Statement.RETURN_GENERATED_KEYS : 0;
                if (returnGenerated > 0) {
                    try (final PreparedStatement stmt = this.prepareStatement(finalQuery, c, returnGenerated)) {
                        statementFiller().fillStatement(finalQuery, new LinkedList<>(query.values().keySet()),
                                new LinkedList<>(query.values().values()), stmt);
                        final DatabaseResultField<T> result = getResult(finalQuery, query.columnToReturn(), stmt,
                                query.returnGeneratedKey());
                        returnValueFuture.complete(result);
                    }
                } else {
                    try (final PreparedStatement stmt = this.prepareStatement(finalQuery, c, statementOptions())) {
                        returnValueFuture.complete(getResult(finalQuery, query.columnToReturn(),
                                statementFiller().fillStatement(finalQuery, new LinkedList<>(query.values().keySet()),
                                        new LinkedList<>(query.values().values()), stmt),
                                query.returnGeneratedKey()));
                    }
                }
            } catch (final SQLException e) {
                returnValueFuture.completeExceptionally(e);
            }
        }, config().threadPool());

        return returnValueFuture;
    }

    int statementOptions();

    /**
     * Get result.
     *
     * @param finalQuery     Final query.
     * @param columnToSelect Column to select.
     * @param stmt           Statement.
     * @return DatabaseResultField of type T.
     * @throws SQLException if sql fails.
     */
    default DatabaseResultField<T> getResult(String finalQuery, DatabaseField<T> columnToSelect, PreparedStatement stmt,
                                             boolean returnGeneratedKey) throws SQLException {
        synchronized (stmt) {
            try {
                if (stmt.isClosed()) {
                    throw new SQLException(String.format("Statement %s closed unexpectedly", stmt.toString()));
                }
                stmt.executeUpdate();
                if (!returnGeneratedKey) {
                    return new DatabaseResultField<>(columnToSelect, null);
                }
                return getGeneratedKeys(finalQuery, columnToSelect, stmt);
            } catch (final SQLException e) {
                throw new SQLException(String.format("%s, query %s", e.getMessage(), finalQuery), e);
            }
        }
    }

    default DatabaseResultField<T> getGeneratedKeys(String finalQuery, DatabaseField<T> columnToSelect,
                                                    PreparedStatement stmt) throws SQLException {
        synchronized (stmt) {
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
