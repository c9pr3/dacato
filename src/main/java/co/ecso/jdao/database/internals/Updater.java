package co.ecso.jdao.database.internals;

import co.ecso.jdao.config.ConfigGetter;
import co.ecso.jdao.database.query.DatabaseField;
import co.ecso.jdao.database.query.SingleColumnUpdateQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Updater.
 *
 * @param <T> Type of update, p.e. Long -> Type of query.
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 11.09.16
 */
public interface Updater<T> extends ConfigGetter {

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
     * Update entry.
     *
     * @param query Query.
     * @return Number of affected rows.
     */
    default CompletableFuture<Integer> update(final SingleColumnUpdateQuery<T> query) {
        final CompletableFuture<Integer> returnValueFuture = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try {
                final List<DatabaseField<?>> newArr = new LinkedList<>();
                newArr.addAll(query.columnValuesToSet().keySet());
                newArr.add(query.whereColumn());
                final String finalQuery = String.format(query.query(), newArr.toArray());
                try (final Connection c = config().databaseConnectionPool().getConnection()) {
                    try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                        final List<Object> values = new LinkedList<>();
                        query.columnValuesToSet().values().forEach(values::add);
                        values.add(query.whereValue());
                        statementFiller().fillStatement(newArr, values, stmt);
                        returnValueFuture.complete(getResult(stmt));
                    }
                }
            } catch (final Exception e) {
                returnValueFuture.completeExceptionally(e);
            }
        }, config().threadPool());
        return returnValueFuture;
    }

    /**
     * Get result.
     *
     * @param stmt Statement.
     * @return Result.
     * @throws SQLException if query fails.
     */
    default int getResult(final PreparedStatement stmt) throws SQLException {
        return stmt.executeUpdate();
    }

}
