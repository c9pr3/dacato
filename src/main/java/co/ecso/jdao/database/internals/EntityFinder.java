package co.ecso.jdao.database.internals;

import co.ecso.jdao.config.ConfigGetter;
import co.ecso.jdao.database.ColumnList;
import co.ecso.jdao.database.SQLNoResultException;
import co.ecso.jdao.database.query.DatabaseField;
import co.ecso.jdao.database.query.DatabaseResultField;
import co.ecso.jdao.database.query.MultiColumnQuery;
import co.ecso.jdao.database.query.SingleColumnQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * EntityFinder.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 11.09.16
 */
public interface EntityFinder extends ConfigGetter {

    default StatementFiller statementFiller() {
        return new StatementFiller() {
        };
    }

    /**
     * Find many.
     *
     * @param <W>   Type to select. P.e. String.
     * @param query Query.
     * @param validityCheck Validity check callback.
     * @return List of DatabaseResultFields with type to select (W), p.e. String
     */
    default <S, W> CompletableFuture<List<DatabaseResultField<S>>> findMany(final SingleColumnQuery<S, W> query,
                                                                            final Callable<AtomicBoolean>
                                                                                    validityCheck) {
        final CompletableFuture<List<DatabaseResultField<S>>> returnValueFuture = new CompletableFuture<>();

        if (validityFails(validityCheck, returnValueFuture)) {
            return returnValueFuture;
        }

        CompletableFuture.runAsync(() -> {
            final DatabaseField<S> columnToSelect = query.columnToSelect();
            final DatabaseField<W> columnWhere = query.columnWhere();

            final String finalQuery = String.format(query.query(), columnToSelect.name(),
                    columnWhere != null ? columnWhere.name() : null);

            try (final Connection c = config().databaseConnectionPool().getConnection()) {
                try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                    final PreparedStatement filledStatement = statementFiller().fillStatement(
                            Collections.singletonList(columnWhere),
                            Collections.singletonList(query.columnWhereValue()), stmt);
                    final List<DatabaseResultField<S>> listRowResult = getListRowResult(finalQuery, columnToSelect,
                            filledStatement);
                    returnValueFuture.complete(listRowResult);
                }
            } catch (final Exception e) {
                returnValueFuture.completeExceptionally(e);
            }
        }, config().threadPool());
        return returnValueFuture;
    }

    default boolean validityFails(final Callable<AtomicBoolean> validityCheck,
                                  final CompletableFuture<?> returnValueFuture) {
        try {
            if (!validityCheck.call().get()) {
                returnValueFuture.completeExceptionally(new IllegalArgumentException("Object already destroyed"));
                return true;
            }
        } catch (final Exception e) {
            returnValueFuture.completeExceptionally(e);
            return true;
        }
        return false;
    }

    /**
     * Find one.
     *
     * @param query Query.
     * @param validityCheck Validity check callback.
     * @param <S>   Type to return, p.e. String in select x from y where name = z.
     * @return DatabaseResultField with type to select (S), p.e. String
     */
    default <S> CompletableFuture<DatabaseResultField<S>> findOne(final MultiColumnQuery<S> query,
                                                                  final Callable<AtomicBoolean> validityCheck) {

        final CompletableFuture<DatabaseResultField<S>> returnValueFuture = new CompletableFuture<>();

        if (validityFails(validityCheck, returnValueFuture)) {
            return returnValueFuture;
        }

        CompletableFuture.runAsync(() -> {
            final DatabaseField<S> columnToSelect = query.columnToSelect();
            final ColumnList valuesWhere = query.values();
            final List<Object> format = new ArrayList<>();

            format.add(columnToSelect.name());
            format.addAll(valuesWhere.values().keySet());
            final String finalQuery = String.format(query.query(), format.toArray());

            try (final Connection c = config().databaseConnectionPool().getConnection()) {
                try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                    final PreparedStatement filledStatement = statementFiller().fillStatement(
                            new LinkedList<>(valuesWhere.values().keySet()),
                            new LinkedList<>(valuesWhere.values().values()), stmt);
                    final DatabaseResultField<S> singleRowResult = getSingleRowResult(finalQuery, columnToSelect,
                            filledStatement);
                    returnValueFuture.complete(singleRowResult);
                }
            } catch (final Exception e) {
                returnValueFuture.completeExceptionally(e);
            }
        }, config().threadPool());

        return returnValueFuture;
    }

    /**
     * Find One.
     *
     * @param <W>   Type to return, p.e. String in select x from y where name = z.
     * @param query Query.
     * @param validityCheck Validity check callback.
     * @return DatabaseResultField with type to select (W), p.e. String
     */
    default <S, W> CompletableFuture<DatabaseResultField<S>> findOne(final SingleColumnQuery<S, W> query,
                                                                     final Callable<AtomicBoolean> validityCheck) {

        final CompletableFuture<DatabaseResultField<S>> returnValueFuture = new CompletableFuture<>();

        if (validityFails(validityCheck, returnValueFuture)) {
            return returnValueFuture;
        }

        CompletableFuture.runAsync(() -> {
            final DatabaseField<S> columnToSelect = query.columnToSelect();
            final DatabaseField<W> columnWhere = query.columnWhere();
            final W whereValueToFind = query.columnWhereValue();
            final List<Object> format = new ArrayList<>();

            format.add(columnToSelect.name());
            format.add(columnWhere.name());
            final String finalQuery = String.format(query.query(), format.toArray());
            try (final Connection c = config().databaseConnectionPool().getConnection()) {
                try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                    final PreparedStatement filledStatement = statementFiller().fillStatement(
                            Collections.singletonList(columnToSelect),
                            Collections.singletonList(whereValueToFind), stmt);
                    final DatabaseResultField<S> singleRowResult = getSingleRowResult(finalQuery, columnToSelect,
                            filledStatement);
                    returnValueFuture.complete(singleRowResult);
                }
            } catch (final Exception e) {
                returnValueFuture.completeExceptionally(e);
            }
        }, config().threadPool());
        return returnValueFuture;
    }

    /**
     * Get list row result.
     *
     * @param finalQuery     Query.
     * @param columnToSelect Column to select.
     * @param stmt           Statement.
     * @param <R>            Type to return, p.e. String. Must match Type of columnToSelect.
     * @return List of DatabaseResultFields with type W, p.e. String.
     * @throws SQLException if SQL fails.
     */
    default <R> List<DatabaseResultField<R>> getListRowResult(final String finalQuery,
                                                              final DatabaseField<R> columnToSelect,
                                                              final PreparedStatement stmt) throws SQLException {
        final List<DatabaseResultField<R>> result = new LinkedList<>();
        try (final ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                final R rval = rs.getObject(1, columnToSelect.valueClass());
                if (rval == null) {
                    result.add(new DatabaseResultField<>(columnToSelect, null));
                } else {
                    if (columnToSelect.valueClass() == String.class) {
                        result.add(new DatabaseResultField<>(columnToSelect,
                                columnToSelect.valueClass().cast(rval.toString().trim())));
                    } else if (columnToSelect.valueClass() == Boolean.class) {
                        final Boolean boolVal = "1".equals(rval.toString().trim());
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
     * @param finalQuery     Query.
     * @param columnToSelect Which column to select.
     * @param stmt           Statement.
     * @param <R>            Type to return, p.e. String.
     * @return DatabaseResultField with type W, p.e. String.
     * @throws SQLException if SQL fails.
     */
    default <R> DatabaseResultField<R> getSingleRowResult(final String finalQuery,
                                                          final DatabaseField<R> columnToSelect,
                                                          final PreparedStatement stmt) throws SQLException {
        final DatabaseResultField<R> result;
        try (final ResultSet rs = stmt.executeQuery()) {
            if (!rs.next()) {
                throw new SQLNoResultException(String.format("No Results for %s", finalQuery));
            }
            final R rval = rs.getObject(1, columnToSelect.valueClass());
            if (rval == null) {
                result = new DatabaseResultField<>(columnToSelect, null);
            } else {
                if (columnToSelect.valueClass() == String.class) {
                    result = new DatabaseResultField<>(columnToSelect,
                            columnToSelect.valueClass().cast(rval.toString().trim()));
                } else if (columnToSelect.valueClass() == Boolean.class) {
                    final Boolean boolVal = "1".equals(rval.toString().trim());
                    result = new DatabaseResultField<>(columnToSelect, columnToSelect.valueClass().cast(boolVal));
                } else {
                    result = new DatabaseResultField<>(columnToSelect, rval);
                }
            }
        }
        return result;
    }
}
