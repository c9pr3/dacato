package co.ecso.dacato.database.query;

import co.ecso.dacato.config.ConfigGetter;
import co.ecso.dacato.database.querywrapper.DatabaseField;
import co.ecso.dacato.database.querywrapper.SingleColumnUpdateQuery;
import co.ecso.dacato.database.transaction.Transaction;

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
 * @author Christian Scharmach (cs@e-cs.co)
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
            Connection c = null;
            Integer result = null;
            try {
                final String finalQuery = String.format(query.query(), newArr.toArray());
                c = connection();
                if (c == null) {
                    throw new SQLException("Could not obtain connection");
                }
                try (final PreparedStatement stmt = this.prepareStatement(finalQuery, c, this.statementOptions())) {
                    query.columnValuesToSet().values().forEach(values::add);
                    values.add(query.whereValue());
                    result = getResult(finalQuery,
                            statementFiller().fillStatement(finalQuery, newArr, values, stmt, c), c);
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

    default Transaction transaction() {
        return null;
    }

    default Connection connection() throws SQLException {
        return config().databaseConnectionPool().getConnection();
    }

    int statementOptions();

    /**
     * Get result.
     *
     * @param stmt Statement.
     * @param c Connection.
     * @return Result.
     * @throws SQLException if query fails.
     */
    default int getResult(final String finalQuery, final PreparedStatement stmt, final Connection c)
            throws SQLException {
        synchronized (c) {
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
