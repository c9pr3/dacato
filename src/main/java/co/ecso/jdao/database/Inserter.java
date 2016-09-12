package co.ecso.jdao.database;

import co.ecso.jdao.config.ConfigGetter;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Inserter.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 12.09.16
 */
@SuppressWarnings("Duplicates")
interface Inserter<T, R extends DatabaseEntity<T>> extends StatementFiller, ConfigGetter {

    default CompletableFuture<DatabaseResultField<T>> add(final InsertQuery<T> query) {

        final CompletableFuture<DatabaseResultField<T>> returnValueFuture = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            final List<DatabaseField<?>> keys = new LinkedList<>();
            keys.add(query.columnToReturn());
            keys.addAll(query.values().keySet());
            final String finalQuery = String.format(query.query(), keys.toArray());
            try (final Connection c = config().getConnectionPool().getConnection()) {
                try (final PreparedStatement stmt = c.prepareStatement(finalQuery, Statement.RETURN_GENERATED_KEYS)) {
                    fillStatement(keys, new LinkedList<>(query.values().values()), stmt);
                    returnValueFuture.complete(getResult(finalQuery, query.columnToReturn(), stmt));
                }
            } catch (final SQLException e) {
                returnValueFuture.completeExceptionally(e);
            }
        }, config().getThreadPool());

        return returnValueFuture;
    }

    default DatabaseResultField<T> getResult(final String finalQuery,
                                             final DatabaseField<T> columnToSelect,
                                             final PreparedStatement stmt) throws SQLException {
        stmt.executeUpdate();
        try (final ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (!generatedKeys.next()) {
                throw new SQLException(String.format("Query %s failed, resultset empty", finalQuery));
            }
            //noinspection unchecked
            return new DatabaseResultField<T>(columnToSelect, generatedKeys.getObject(1, columnToSelect.valueClass()));
        }
    }

}
