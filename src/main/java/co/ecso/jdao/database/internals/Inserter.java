package co.ecso.jdao.database.internals;

import co.ecso.jdao.config.ConfigGetter;
import co.ecso.jdao.database.query.DatabaseField;
import co.ecso.jdao.database.query.DatabaseResultField;
import co.ecso.jdao.database.query.InsertQuery;

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
public interface Inserter<T> extends ConfigGetter {

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
    default CompletableFuture<DatabaseResultField<T>> add(final InsertQuery<T> query) {
        final CompletableFuture<DatabaseResultField<T>> returnValueFuture = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            final List<DatabaseField<?>> keys = new LinkedList<>();
            if (query.returnGeneratedKey()) {
                keys.add(query.columnToReturn());
            }
            keys.addAll(query.values().keySet());
            final String finalQuery = String.format(query.query(), keys.toArray());
            try (final Connection c = config().databaseConnectionPool().getConnection()) {
                final int returnGenerated = query.returnGeneratedKey() ? Statement.RETURN_GENERATED_KEYS
                        : Statement.NO_GENERATED_KEYS;
                try (final PreparedStatement stmt = c.prepareStatement(finalQuery, returnGenerated)) {
                    statementFiller().fillStatement(finalQuery, new LinkedList<>(query.values().keySet()),
                            new LinkedList<>(query.values().values()), stmt);
                    returnValueFuture.complete(getResult(finalQuery, query.columnToReturn(), stmt,
                            query.returnGeneratedKey()));
                }
            } catch (final SQLException e) {
                returnValueFuture.completeExceptionally(e);
            }
        }, config().threadPool());

        return returnValueFuture;
    }

    /**
     * Get result.
     *
     * @param finalQuery     Final query.
     * @param columnToSelect Column to select.
     * @param stmt           Statement.
     * @return DatabaseResultField of type T.
     * @throws SQLException if sql fails.
     */
    default DatabaseResultField<T> getResult(final String finalQuery,
                                             final DatabaseField<T> columnToSelect,
                                             final PreparedStatement stmt,
                                             final boolean returnGeneratedKey) throws SQLException {
        try {
            stmt.executeUpdate();
            if (!returnGeneratedKey) {
                return new DatabaseResultField<>(columnToSelect, null);
            }
            try (final ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new SQLException(String.format("Query %s failed, resultset empty", finalQuery));
                }
                //noinspection unchecked
                return new DatabaseResultField<>(columnToSelect,
                        generatedKeys.getObject(1, columnToSelect.valueClass()));
            }
        } catch (final SQLException e) {
            throw new SQLException(String.format("%s, query %s", e.getMessage(), finalQuery), e);
        }
    }

}
