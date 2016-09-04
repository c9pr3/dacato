package co.ecso.jdao.database;

import co.ecso.jdao.config.ConfigGetter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * SingleColumnFinder.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 02.09.16
 */
interface SingleColumnFinder extends ConfigGetter, StatementFiller {

    default <R> CompletableFuture<List<R>> find(final ListFindQuery<R> query) {
        final DatabaseField<R> columnToSelect = query.columnToSelect();

        final CompletableFuture<List<R>> returnValueFuture = new CompletableFuture<>();

        //noinspection Duplicates
        final List<Object> format = new ArrayList<>();
        final Map<DatabaseField<?>, CompletableFuture<?>> whereFuture = query.whereFutureMap();
        final List<?> whereList = whereFuture.values().stream().map(CompletableFuture::join)
                .collect(Collectors.toList());

        CompletableFuture.runAsync(() -> {
            format.add(columnToSelect);
            whereFuture.keySet().forEach(format::add);
            //find a way to find out if format.toArray has the right amount of entries needed to solve query.query()
            final String finalQuery = String.format(query.query(), format.toArray());

            try (final Connection c = config().getConnectionPool().getConnection()) {
                try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                    returnValueFuture.complete(getListRowResult(finalQuery, columnToSelect,
                            fillStatement(Collections.singletonList(columnToSelect), whereList, stmt)));
                }
            } catch (final Exception e) {
                returnValueFuture.completeExceptionally(e);
            }
        }, config().getThreadPool());
        return returnValueFuture;
    }

    default <R> CompletableFuture<R> find(final SingleFindQuery<R> query) {
        final DatabaseField<R> columnToSelect = query.columnSelect();
        final Map<DatabaseField<?>, CompletableFuture<?>> whereFuture = query.whereFuture();

        final CompletableFuture<R> returnValueFuture = new CompletableFuture<>();

        //noinspection Duplicates
        final List<Object> format = new ArrayList<>();
        final List<?> whereList = whereFuture.values().stream().map(CompletableFuture::join)
                .collect(Collectors.toList());
        CompletableFuture.runAsync(() -> {
            format.add(columnToSelect);
            whereFuture.keySet().forEach(format::add);
            //find a way to find out if format.toArray has the right amount of entries needed to solve query.query()
            final String finalQuery = String.format(query.query(), format.toArray());

            try (final Connection c = config().getConnectionPool().getConnection()) {
                try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                    final PreparedStatement filledStatement = fillStatement(
                            new LinkedList<>(whereFuture.keySet()), whereList, stmt);
                    final R singleRowResult =  getSingleRowResult(finalQuery, columnToSelect, filledStatement);
                    returnValueFuture.complete(singleRowResult);
                }
            } catch (final Exception e) {
                returnValueFuture.completeExceptionally(e);
            }
        }, config().getThreadPool());
        return returnValueFuture;
    }

    //* @todo map back to DatabaseField with value rather than types.
    default <R> List<R> getListRowResult(final String finalQuery, final DatabaseField<R> columnToSelect,
                               final PreparedStatement stmt) throws SQLException {
        final List<R> rValList = new LinkedList<>();
        try (final ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                final R rval = (R) rs.getObject(1, columnToSelect.valueClass());
                if (rval == null) {
                    rValList.add(null);
                } else {
                    if (columnToSelect.valueClass() == String.class) {
                        //noinspection unchecked
                        rValList.add((R) rval.toString().trim());
                    } else if (columnToSelect.valueClass() == Boolean.class) {
                        final Boolean boolVal = rval.toString().trim().equals("1");
                        //noinspection unchecked
                        rValList.add((R) boolVal);
                    } else {
                        rValList.add(rval);
                    }
                }
            }
        }
        return rValList;
    }

    //* @todo map back to DatabaseField with value rather than types.
    default <R> R getSingleRowResult(final String finalQuery, final DatabaseField<R> columnToSelect,
                                 final PreparedStatement stmt) throws SQLException {
        try (final ResultSet rs = stmt.executeQuery()) {
            if (!rs.next()) {
                throw new SQLException(String.format("No Results for %s", finalQuery));
            }
            final R rval = (R) rs.getObject(1, columnToSelect.valueClass());
            if (rval == null) {
                return null;
            } else {
                if (columnToSelect.valueClass() == String.class) {
                    //noinspection unchecked
                    return (R) rval.toString().trim();
                } else if (columnToSelect.valueClass() == Boolean.class) {
                    final Boolean boolVal = rval.toString().trim().equals("1");
                    //noinspection unchecked
                    return (R) boolVal;
                } else {
                    return rval;
                }
            }
        }
    }
}
