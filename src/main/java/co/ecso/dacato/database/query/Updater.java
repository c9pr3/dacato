package co.ecso.dacato.database.query;

import co.ecso.dacato.config.ConfigGetter;
import co.ecso.dacato.database.querywrapper.DatabaseField;
import co.ecso.dacato.database.querywrapper.SingleColumnUpdateQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Updater.
 *
 * @param <T> Type of update, p.e. Long -> Type of query.
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 11.09.16
 */
public interface Updater<T> extends ConfigGetter, StatementPreparer {

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
     * @param query         Query.
     * @param validityCheck Validity check callback.
     * @return Number of affected rows.
     */
    default CompletableFuture<Integer> update(SingleColumnUpdateQuery<T> query, Callable<AtomicBoolean> validityCheck) {
        final CompletableFuture<Integer> returnValueFuture = new CompletableFuture<>();

        try {
            if (!validityCheck.call().get()) {
                returnValueFuture.completeExceptionally(new IllegalArgumentException("Object already destroyed"));
                return returnValueFuture;
            }
        } catch (final Exception e) {
            returnValueFuture.completeExceptionally(e);
            return returnValueFuture;
        }
        final List<DatabaseField<?>> newArr = new LinkedList<>();
        newArr.addAll(query.columnValuesToSet().keySet());
        newArr.add(query.whereColumn());
        final List<Object> values = new LinkedList<>();

        CompletableFuture.runAsync(() -> {
            try {
                final String finalQuery = String.format(query.query(), newArr.toArray());
                try (final Connection c = config().databaseConnectionPool().getConnection()) {
                    try (final PreparedStatement stmt = this.prepareStatement(finalQuery, c, this.statementOptions())) {
                        query.columnValuesToSet().values().forEach(values::add);
                        values.add(query.whereValue());
                        returnValueFuture.complete(getResult(finalQuery,
                                statementFiller().fillStatement(finalQuery, newArr, values, stmt)));
                    }
                }
            } catch (final Exception e) {
                returnValueFuture.completeExceptionally(e);
            }
        }, config().threadPool());
        return returnValueFuture;
    }

    int statementOptions();

    /**
     * Get result.
     *
     * @param stmt Statement.
     * @return Result.
     * @throws SQLException if query fails.
     */
    default int getResult(final String finalQuery, final PreparedStatement stmt) throws SQLException {
        synchronized (stmt) {
            if (stmt.isClosed()) {
                throw new SQLException(String.format("Statement %s closed unexpectedly", stmt.toString()));
            }
            try {
                return stmt.executeUpdate();
            } catch (final SQLException e) {
                throw new SQLException(String.format("%s, query %s", e.getMessage(), finalQuery), e);
            }
        }
    }

}
