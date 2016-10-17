package co.ecso.dacato.database.query;

import co.ecso.dacato.config.ConfigGetter;
import co.ecso.dacato.database.ColumnList;
import co.ecso.dacato.database.querywrapper.RemoveQuery;
import co.ecso.dacato.database.statement.StatementFiller;
import co.ecso.dacato.database.statement.StatementPreparer;

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

    default <S> CompletableFuture<Integer> removeOne(final RemoveQuery<S> query) {
        final CompletableFuture<Integer> returnValueFuture = new CompletableFuture<>();

        final ColumnList valuesWhere = query.values();
        final List<Object> format = new ArrayList<>();

        format.addAll(valuesWhere.values().keySet());
        final String finalQuery = String.format(query.query(), format.toArray());

        CompletableFuture.runAsync(() -> {
            try (final Connection c = config().databaseConnectionPool().getConnection()) {
                try (final PreparedStatement stmt = this.prepareStatement(finalQuery, c, this.statementOptions())) {
                    final PreparedStatement filledStatement = statementFiller().fillStatement(finalQuery,
                            new LinkedList<>(valuesWhere.values().keySet()),
                            new LinkedList<>(valuesWhere.values().values()), stmt);
                    final Integer singleRowResult = getSingleRowResult(filledStatement);
                    returnValueFuture.complete(singleRowResult);
                }
            } catch (final Exception e) {
                returnValueFuture.completeExceptionally(e);
            }
        }, config().threadPool());

        return returnValueFuture;
    }

    int statementOptions();

    /**
     * Get single row result.
     *
     * @param stmt Statement.
     * @return DatabaseResultField with type W, p.e. String.
     * @throws SQLException if SQL fails.
     */
    default Integer getSingleRowResult(final PreparedStatement stmt) throws SQLException {
        if (stmt.isClosed()) {
            throw new SQLException(String.format("Statement %s closed unexpectedly", stmt.toString()));
        }
        return stmt.executeUpdate();
    }

}
