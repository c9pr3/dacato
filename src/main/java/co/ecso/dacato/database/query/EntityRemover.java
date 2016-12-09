package co.ecso.dacato.database.query;

import co.ecso.dacato.config.ConfigGetter;
import co.ecso.dacato.database.ColumnList;
import co.ecso.dacato.database.querywrapper.RemoveQuery;
import co.ecso.dacato.database.transaction.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * EntityRemover.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 01.10.16
 */
public interface EntityRemover extends ConfigGetter, StatementPreparer {

    default StatementFiller statementFiller() {
        return new StatementFiller() {
        };
    }

    default <S> CompletableFuture<Integer> removeOne(RemoveQuery<S> query) {
        final CompletableFuture<Integer> returnValueFuture = new CompletableFuture<>();

        final ColumnList valuesWhere = query.values();
        final List<Object> format = new ArrayList<>();

        format.addAll(valuesWhere.values().keySet());
        final String finalQuery = String.format(query.query(), format.toArray());

        CompletableFuture.runAsync(() -> {
            Connection c = null;
            Integer singleRowResult = null;
            try {
                c = config().databaseConnectionPool().getConnection();
                if (c == null) {
                    throw new SQLException("Could not obtain connection");
                }
                try (final PreparedStatement stmt = this.prepareStatement(finalQuery, c, this.statementOptions())) {
                    final PreparedStatement filledStatement = statementFiller().fillStatement(finalQuery,
                            new LinkedList<>(valuesWhere.values().keySet()),
                            new LinkedList<>(valuesWhere.values().values()), stmt);
                    singleRowResult = getSingleRowResult(filledStatement);
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
                    returnValueFuture.complete(singleRowResult);
                }
            }
        }, config().threadPool());

        return returnValueFuture;
    }

    default Transaction transaction() {
        return null;
    }

    int statementOptions();

    /**
     * Get single row result.
     *
     * @param stmt Statement.
     * @return DatabaseResultField with type W, p.e. String.
     * @throws SQLException if SQL fails.
     */
    default Integer getSingleRowResult(PreparedStatement stmt) throws SQLException {
        synchronized (stmt) {
            if (stmt.isClosed()) {
                throw new SQLException(String.format("Statement %s closed unexpectedly", stmt.toString()));
            }
            return stmt.executeUpdate();
        }
    }

    default Connection connection() throws SQLException {
        return config().databaseConnectionPool().getConnection();
    }
}
