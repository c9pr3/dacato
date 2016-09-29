package co.ecso.jdao.database.internals;

import co.ecso.jdao.config.ConfigGetter;
import co.ecso.jdao.database.ColumnList;
import co.ecso.jdao.database.SQLNoResultException;
import co.ecso.jdao.database.query.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
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
     * Check if validity fails.
     *
     * @param validityCheck     Validity check callable.
     * @param returnValueFuture ReturnValue to complete exceptionally if validity fails.
     * @return true or false for early return purposes.
     */
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
     * @param query         Query.
     * @param validityCheck Validity check callback.
     * @param <S>           Type to return, p.e. String in select x from y where name = z.
     * @return DatabaseResultField with type to select (S), p.e. String
     */
    default <S> CompletableFuture<DatabaseResultField<S>> findOne(final MultiColumnQuery<S> query,
                                                                  final Callable<AtomicBoolean> validityCheck) {

        final CompletableFuture<DatabaseResultField<S>> returnValueFuture = new CompletableFuture<>();

        if (validityFails(validityCheck, returnValueFuture)) {
            return returnValueFuture;
        }
        final DatabaseField<S> columnToSelect = query.columnToSelect();
        final ColumnList valuesWhere = query.values();
        final List<Object> format = new ArrayList<>();

        format.add(columnToSelect.name());
        format.addAll(valuesWhere.values().keySet());
        final String finalQuery = String.format(query.query(), format.toArray());

        CompletableFuture.runAsync(() -> {
            try (final Connection c = config().databaseConnectionPool().getConnection()) {
                try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                    final PreparedStatement filledStatement = statementFiller().fillStatement(finalQuery,
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
     * @param <W>           Type to return, p.e. String in select x from y where name = z.
     * @param query         Query.
     * @param validityCheck Validity check callback.
     * @return DatabaseResultField with type to select (W), p.e. String
     */
    default <S, W> CompletableFuture<DatabaseResultField<S>> findOne(final SingleColumnQuery<S, W> query,
                                                                     final Callable<AtomicBoolean> validityCheck) {

        final CompletableFuture<DatabaseResultField<S>> returnValueFuture = new CompletableFuture<>();

        if (validityFails(validityCheck, returnValueFuture)) {
            return returnValueFuture;
        }
        final DatabaseField<S> columnToSelect = query.columnToSelect();
        final DatabaseField<W> columnWhere = query.columnWhere();
        final W whereValueToFind = query.columnWhereValue();
        final List<Object> format = new ArrayList<>();

        format.add(columnToSelect.name());
        format.add(columnWhere.name());
        final String finalQuery = String.format(query.query(), format.toArray());

        CompletableFuture.runAsync(() -> {
            try (final Connection c = config().databaseConnectionPool().getConnection()) {
                try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                    final PreparedStatement filledStatement = statementFiller().fillStatement(finalQuery,
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
     * Find one.
     *
     * @param query         Query.
     * @param validityCheck Validity Check.
     * @return List of DatabaseResultFields.
     */
    default CompletableFuture<Map<DatabaseField, DatabaseResultField>> findOne(final MultiColumnSelectQuery<?> query,
                                                                               final Callable<AtomicBoolean>
                                                                                       validityCheck) {

        final CompletableFuture<Map<DatabaseField, DatabaseResultField>> returnValueFuture =
                new CompletableFuture<>();

        if (validityFails(validityCheck, returnValueFuture)) {
            return returnValueFuture;
        }
        final List<DatabaseField> columnsToSelect = query.columnsToSelect();
        final ColumnList valuesWhere = query.values();
        final List<Object> format = new ArrayList<>();

        columnsToSelect.forEach(c -> format.add(c.name()));
        format.addAll(valuesWhere.values().keySet());
        final String finalQuery = String.format(query.query(), format.toArray());

        CompletableFuture.runAsync(() -> {
            try (final Connection c = config().databaseConnectionPool().getConnection()) {
                try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                    final PreparedStatement filledStatement = statementFiller().fillStatement(finalQuery,
                            new LinkedList<>(valuesWhere.values().keySet()),
                            new LinkedList<>(valuesWhere.values().values()), stmt);

                    final Map<DatabaseField, DatabaseResultField> listRowResult = getMapRowResult(finalQuery,
                            columnsToSelect, filledStatement);
                    returnValueFuture.complete(listRowResult);
                }
            } catch (final Exception e) {
                returnValueFuture.completeExceptionally(e);
            }
        }, config().threadPool());

        return returnValueFuture;
    }

    /**
     * Find one.
     *
     * @param query         Query.
     * @param validityCheck Validity Check.
     * @return List of DatabaseResultFields.
     */
    default CompletableFuture<List<Map<DatabaseField, DatabaseResultField>>> findMany(
            final MultiColumnSelectQuery<?> query, final Callable<AtomicBoolean> validityCheck) {

        final CompletableFuture<List<Map<DatabaseField, DatabaseResultField>>> returnValueFuture =
                new CompletableFuture<>();

        if (validityFails(validityCheck, returnValueFuture)) {
            return returnValueFuture;
        }
        final List<DatabaseField> columnsToSelect = query.columnsToSelect();
        final ColumnList valuesWhere = query.values();
        final List<Object> format = new ArrayList<>();

        columnsToSelect.forEach(c -> format.add(c.name()));
        format.addAll(valuesWhere.values().keySet());
        final String finalQuery = String.format(query.query(), format.toArray());

        CompletableFuture.runAsync(() -> {
            try (final Connection c = config().databaseConnectionPool().getConnection()) {
                try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                    final PreparedStatement filledStatement = statementFiller().fillStatement(finalQuery,
                            new LinkedList<>(valuesWhere.values().keySet()),
                            new LinkedList<>(valuesWhere.values().values()), stmt);

                    final List<Map<DatabaseField, DatabaseResultField>> listRowResult =
                            getListMapRowResult(columnsToSelect, filledStatement);
                    returnValueFuture.complete(listRowResult);
                }
            } catch (final Exception e) {
                returnValueFuture.completeExceptionally(e);
            }
        }, config().threadPool());

        return returnValueFuture;
    }

    /**
     * Find many.
     *
     * @param <W>           Type to select. P.e. String.
     * @param query         Query.
     * @param validityCheck Validity check callback.
     * @return List of DatabaseResultFields with type to select (W), p.e. String
     */
    default <S, W> CompletableFuture<List<DatabaseResultField<S>>> findMany(final SingleColumnQuery<S, W> query,
                                                                            final Callable<AtomicBoolean>
                                                                                    validityCheck) {
        final CompletableFuture<List<DatabaseResultField<S>>> returnValueFuture =
                new CompletableFuture<>();

        if (validityFails(validityCheck, returnValueFuture)) {
            return returnValueFuture;
        }

        final DatabaseField<S> columnToSelect = query.columnToSelect();
        final DatabaseField<W> columnWhere = query.columnWhere();

        final String finalQuery = String.format(query.query(), columnToSelect.name(),
                columnWhere != null ? columnWhere.name() : null);

        CompletableFuture.runAsync(() -> {
            try (final Connection c = config().databaseConnectionPool().getConnection()) {
                try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                    final PreparedStatement filledStatement = statementFiller().fillStatement(finalQuery,
                            Collections.singletonList(columnWhere),
                            Collections.singletonList(query.columnWhereValue()), stmt);
                    final List<DatabaseResultField<S>> listRowResult = getListRowResult(columnToSelect,
                            filledStatement);
                    returnValueFuture.complete(listRowResult);
                }
            } catch (final Exception e) {
                returnValueFuture.completeExceptionally(e);
            }
        }, config().threadPool());
        return returnValueFuture;
    }

    default Map<DatabaseField, DatabaseResultField> getMapRowResult(final String finalQuery,
                                                                    final List<DatabaseField> columnsToSelect,
                                                                    final PreparedStatement stmt) throws SQLException {
        final Map<DatabaseField, DatabaseResultField> result = new LinkedHashMap<>();
        try (final ResultSet rs = stmt.executeQuery()) {
            if (!rs.next()) {
                throw new SQLNoResultException(String.format("No Results for %s", finalQuery));
            }
            listMapColumns(columnsToSelect, rs, result);
        }
        return result;
    }

    default List<Map<DatabaseField, DatabaseResultField>> getListMapRowResult(final List<DatabaseField> columnsToSelect,
                                                                              final PreparedStatement stmt)
            throws SQLException {
        final List<Map<DatabaseField, DatabaseResultField>> result = new LinkedList<>();
        try (final ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<DatabaseField, DatabaseResultField> map = new HashMap<>();
                listMapColumns(columnsToSelect, rs, map);
                result.add(map);
            }
        }
        return result;
    }

    default void listMapColumns(final List<DatabaseField> columnsToSelect, final ResultSet rs,
                                final Map<DatabaseField, DatabaseResultField> map) throws SQLException {
        for (final DatabaseField column : columnsToSelect) {
            final Object rval = rs.getObject(column.name(), column.valueClass());
            if (rval == null) {
                //noinspection unchecked
                map.put(column, new DatabaseResultField<>(column, null));
            } else {
                //noinspection unchecked
                map.put(column, castResultValue(column, rval, column.valueClass()));
            }
        }
    }

    /**
     * Get list row result.
     *
     * @param columnToSelect Column to select.
     * @param stmt           Statement.
     * @param <R>            Type to return, p.e. String. Must match Type of columnToSelect.
     * @return List of DatabaseResultFields with type W, p.e. String.
     * @throws SQLException if SQL fails.
     */
    default <R> List<DatabaseResultField<R>> getListRowResult(final DatabaseField<R> columnToSelect,
                                                              final PreparedStatement stmt) throws SQLException {
        final List<DatabaseResultField<R>> result = new LinkedList<>();
        try (final ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                final R rval = rs.getObject(1, columnToSelect.valueClass());
                if (rval == null) {
                    result.add(new DatabaseResultField<>(columnToSelect, null));
                } else {
                    result.add(castResultValue(columnToSelect, rval, columnToSelect.valueClass()));
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
                result = castResultValue(columnToSelect, rval, columnToSelect.valueClass());
            }
        }
        return result;
    }

    /**
     * Cast result field to proper value.
     * Mostly needed for String-trim and boolean 1 to true, 0 to false.
     *
     * @param <R>            Column Type.
     * @param columnToSelect Column to select.
     * @param databaseValue  Value from database.
     * @param valueClass     Value class.
     */
    default <R> DatabaseResultField<R> castResultValue(final DatabaseField<R> columnToSelect,
                                                       final R databaseValue, final Class valueClass) {
        final DatabaseResultField<R> result;
        if (valueClass == String.class) {
            result = new DatabaseResultField<>(columnToSelect,
                    columnToSelect.valueClass().cast(databaseValue.toString().trim()));

        } else if (valueClass == Boolean.class) {
            final Boolean boolVal = "1".equals(databaseValue.toString().trim());
            result = new DatabaseResultField<>(columnToSelect,
                    columnToSelect.valueClass().cast(boolVal));

        } else {
            result = new DatabaseResultField<>(columnToSelect, databaseValue);
        }
        return result;
    }

}
