package co.ecso.jdao;

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
 * @param <T> value to Return, i.E. Long
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 02.09.16
 */
public interface SingleColumnFinder<T> extends ConfigGetter, StatementFiller {

    @SuppressWarnings("Duplicates")
    default CompletableFuture<List<T>> find(final ListFindQuery<T> query) {
        final DatabaseField<T> columnToSelect = query.columnToSelect();

        final CompletableFuture<List<T>> returnValueFuture = new CompletableFuture<>();

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

            try (Connection c = config().getConnectionPool().getConnection()) {
                try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                    returnValueFuture.complete(getListRowResult(finalQuery, columnToSelect,
                            fillStatement(finalQuery, Collections.singletonList(columnToSelect), whereList, stmt)));
                }
            } catch (final Exception e) {
                returnValueFuture.completeExceptionally(e);
            }
        }, config().getThreadPool());
        return returnValueFuture;
    }

    default CompletableFuture<T> find(final SingleFindQuery<T> query) {
        final DatabaseField<T> columnToSelect = query.columnSelect();
        final Map<DatabaseField<?>, CompletableFuture<?>> whereFuture = query.whereFuture();

        final CompletableFuture<T> returnValueFuture = new CompletableFuture<>();

        //noinspection Duplicates
        final List<Object> format = new ArrayList<>();
        final List<?> whereList = whereFuture.values().stream().map(CompletableFuture::join)
                .collect(Collectors.toList());
        CompletableFuture.runAsync(() -> {
            format.add(columnToSelect);
            whereFuture.keySet().forEach(format::add);
            //find a way to find out if format.toArray has the right amount of entries needed to solve query.query()
            final String finalQuery = String.format(query.query(), format.toArray());

            try (Connection c = config().getConnectionPool().getConnection()) {
                try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                    returnValueFuture.complete(getSingleRowResult(finalQuery, columnToSelect,
                            fillStatement(finalQuery, new LinkedList<>(whereFuture.keySet()), whereList, stmt)));
                }
            } catch (final Exception e) {
                returnValueFuture.completeExceptionally(e);
            }
        }, config().getThreadPool());
        return returnValueFuture;
    }

    //* @todo map back to DatabaseField with value rather than types.
    @SuppressWarnings("Duplicates")
    default List<T> getListRowResult(final String finalQuery, final DatabaseField<T> columnToSelect,
                               final PreparedStatement stmt) throws SQLException {
        List<T> rValList = new LinkedList<>();
        try (final ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                final T rval = (T) rs.getObject(1, columnToSelect.valueClass());
                if (rval == null) {
                    rValList.add(null);
                } else {
                    if (columnToSelect.valueClass() == String.class) {
                        //noinspection unchecked
                        rValList.add((T) rval.toString().trim());
                    } else if (columnToSelect.valueClass() == Boolean.class) {
                        final Boolean boolVal = rval.toString().trim().equals("1");
                        //noinspection unchecked
                        rValList.add((T) boolVal);
                    } else {
                        rValList.add(rval);
                    }
                }
            }
        }
        return rValList;
    }

    //* @todo map back to DatabaseField with value rather than types.
    default T getSingleRowResult(final String finalQuery, final DatabaseField<T> columnToSelect,
                                 final PreparedStatement stmt) throws SQLException {
        try (final ResultSet rs = stmt.executeQuery()) {
            if (!rs.next()) {
                throw new SQLException(String.format("No Results for %s", finalQuery));
            }
            final T rval = (T) rs.getObject(1, columnToSelect.valueClass());
            if (rval == null) {
                return null;
            } else {
                if (columnToSelect.valueClass() == String.class) {
                    //noinspection unchecked
                    return (T) rval.toString().trim();
                } else if (columnToSelect.valueClass() == Boolean.class) {
                    final Boolean boolVal = rval.toString().trim().equals("1");
                    //noinspection unchecked
                    return (T) boolVal;
                } else {
                    return rval;
                }
            }
        }
    }
}
