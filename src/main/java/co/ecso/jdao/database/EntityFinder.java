package co.ecso.jdao.database;

import co.ecso.jdao.config.ConfigGetter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * EntityFinder.
 *
 * @param <T> Type of this entity-row, p.e Long (auto_inc ID is usually type Long)
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 11.09.16
 */
@SuppressWarnings("Duplicates")
interface EntityFinder<T> extends StatementFiller, ConfigGetter {

    /**
     * Find many.
     *
     * @param <R> Type to select. P.e. String.
     * @param query Query.
     * @return List of DatabaseResultFields with type to select (R), p.e. String
     */
    default <R> CompletableFuture<List<DatabaseResultField<R>>> findMany(final SingleColumnQuery<R, T> query) {
        final DatabaseField<R> columnToSelect = query.columnToSelect();

        final CompletableFuture<List<DatabaseResultField<R>>> returnValueFuture = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            final String finalQuery = String.format(query.query(), columnToSelect.name());
            try (final Connection c = config().getConnectionPool().getConnection()) {
                try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                    final PreparedStatement filledStatement = fillStatement(
                            Collections.singletonList(columnToSelect), Collections.emptyList(), stmt);
                    final List<DatabaseResultField<R>> listRowResult = getListRowResult(finalQuery, columnToSelect,
                            filledStatement);
                    returnValueFuture.complete(listRowResult);
                }
            } catch (final Exception e) {
                returnValueFuture.completeExceptionally(e);
            }
        }, config().getThreadPool());
        return returnValueFuture;
    }

    /**
     * Find One.
     * We can find
     *
     * @param query Query.
     * @param <R> Type to Select, p.e. String.
     * @return DatabaseResultField with type to select (R), p.e. String
     */
    default <R> CompletableFuture<DatabaseResultField<R>> findOne(final SingleColumnQuery<R, T> query) {
        final DatabaseField<R> columnToSelect = query.columnToSelect();
        final DatabaseField<T> columnWhere = query.columnWhere();
        final T whereValueToFind = query.columnWhereValue();

        final CompletableFuture<DatabaseResultField<R>> returnValueFuture = new CompletableFuture<>();

        final List<Object> format = new ArrayList<>();
        CompletableFuture.runAsync(() -> {
            format.add(columnToSelect.name());
            format.add(columnWhere.name());
            final String finalQuery = String.format(query.query(), format.toArray());
            try (final Connection c = config().getConnectionPool().getConnection()) {
                try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                    final PreparedStatement filledStatement = fillStatement(
                            Collections.singletonList(columnToSelect),
                            Collections.singletonList(whereValueToFind), stmt);
                    final DatabaseResultField<R> singleRowResult = getSingleRowResult(finalQuery, columnToSelect,
                            filledStatement);
                    returnValueFuture.complete(singleRowResult);
                }
            } catch (final Exception e) {
                returnValueFuture.completeExceptionally(e);
            }
        }, config().getThreadPool());
        return returnValueFuture;
    }

    /**
     * Get list row result.
     *
     * @param finalQuery Query.
     * @param columnToSelect Column to select.
     * @param stmt Statement.
     * @param <R> Type to return, p.e. String. Must match Type of columnToSelect.
     * @return List of DatabaseResultFields with type R, p.e. String.
     * @throws SQLException if SQL fails.
     */
    default <R> List<DatabaseResultField<R>> getListRowResult(final String finalQuery,
                                                              final DatabaseField<R> columnToSelect,
                                                              final PreparedStatement stmt) throws SQLException {
        final List<DatabaseResultField<R>> result = new LinkedList<>();
        try (final ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                final R rval = rs.getObject(1, columnToSelect.valueClass());
                if (rval != null) {
                    if (columnToSelect.valueClass() == String.class) {
                        result.add(new DatabaseResultField<>(columnToSelect,
                                columnToSelect.valueClass().cast(rval.toString().trim())));
                    } else if (columnToSelect.valueClass() == Boolean.class) {
                        final Boolean boolVal = rval.toString().trim().equals("1");
                        result.add(new DatabaseResultField<>(columnToSelect,
                                columnToSelect.valueClass().cast(boolVal)));
                    } else {
                        result.add(new DatabaseResultField<>(columnToSelect, rval));
                    }
                }
            }
        }
        return result;
    }

    /**
     * Get single row result.
     *
     * @param finalQuery Query.
     * @param columnToSelect Which column to select.
     * @param stmt Statement.
     * @param <R> Type to return, p.e. String.
     * @return DatabaseResultField with type R, p.e. String.
     * @throws SQLException if SQL fails.
     */
    default <R> DatabaseResultField<R> getSingleRowResult(final String finalQuery,
                                                          final DatabaseField<R> columnToSelect,
                                                          final PreparedStatement stmt) throws SQLException {
        DatabaseResultField<R> result = null;
        try (final ResultSet rs = stmt.executeQuery()) {
            if (!rs.next()) {
                throw new SQLException(String.format("No Results for %s", finalQuery));
            }
            final R rval = rs.getObject(1, columnToSelect.valueClass());
            if (rval != null) {
                if (columnToSelect.valueClass() == String.class) {
                    result = new DatabaseResultField<>(columnToSelect,
                            columnToSelect.valueClass().cast(rval.toString().trim()));
                } else if (columnToSelect.valueClass() == Boolean.class) {
                    final Boolean boolVal = rval.toString().trim().equals("1");
                    result = new DatabaseResultField<>(columnToSelect, columnToSelect.valueClass().cast(boolVal));
                } else {
                    result = new DatabaseResultField<>(columnToSelect, rval);
                }
            }
        }
        return result;
    }
}
