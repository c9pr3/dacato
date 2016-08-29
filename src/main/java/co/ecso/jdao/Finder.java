package co.ecso.jdao;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Finder.
 *
 * @param <R> ReturnValue, p.e. Long or String
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 28.08.16
 */
public interface Finder<R> extends ConfigGetter {

    default CompletableFuture<R> findOne(final Query query, final DatabaseField<R> columnToReturn,
                                         final CompletableFuture<?> whereFuture) {
        final CompletableFuture<R> returnValueFuture = new CompletableFuture<>();
        whereFuture.thenAccept(whereId -> {
            try {
                final String finalQuery = String.format(query.getQuery(), columnToReturn);
                try (Connection c = config().getConnectionPool().getConnection()) {
                    try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                        stmt.setObject(1, whereId, columnToReturn.sqlType());
                        getResult(query, columnToReturn, returnValueFuture, stmt);
                    }
                }
            } catch (final Exception e) {
                returnValueFuture.completeExceptionally(e);
            }
        });
        return returnValueFuture;
    }

    default CompletableFuture<R> findOne(final Query query, final DatabaseField<R> columnToReturn,
                                         final Map<DatabaseField<?>, ?> columnsToSelect) {
        final CompletableFuture<R> f = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try (Connection c = config().getConnectionPool().getConnection()) {
                final List<DatabaseField> newArr = new LinkedList<>();
                newArr.add(columnToReturn);
                newArr.addAll(columnsToSelect.keySet());
                final String finalQuery = String.format(query.getQuery(), newArr.toArray());
                try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                    fillStatement(columnsToSelect, stmt);
                    getResult(query, columnToReturn, f, stmt);
                }
            } catch (final Exception e) {
                f.completeExceptionally(e);
            }
        }, config().getThreadPool());
        return f;
    }

    default void fillStatement(final Map<DatabaseField<?>, ?> columnsToSelect, final PreparedStatement stmt)
            throws SQLException {
        for (int i = 1; i <= columnsToSelect.size(); i++) {
            final int sqlType = ((DatabaseField) columnsToSelect.keySet().toArray()[i - 1]).sqlType();
            final Object valueToSet = columnsToSelect.values().toArray()[i - 1];
            try {
                stmt.setObject(i, valueToSet, sqlType);
            } catch (final SQLDataException | SQLSyntaxErrorException e) {
                throw new SQLException(String.format("Could not set %s to %d: %s", valueToSet, sqlType, e));
            }
        }
    }

    default CompletableFuture<LinkedList<R>> findMany(final Query query, DatabaseField<?> selector,
                                                      final Map<DatabaseField<?>, ?> columns) {

        final CompletableFuture<LinkedList<R>> returnFuture = new CompletableFuture<>();
        final LinkedList<R> futureList = new LinkedList<>();
        CompletableFuture.runAsync(() -> {
            try {
                LinkedList<DatabaseField<?>> newColumns = new LinkedList<>();
                newColumns.add(selector);
                newColumns.addAll(columns.keySet());
                final String finalQuery = String.format(query.getQuery(), newColumns.toArray());
                try (Connection c = config().getConnectionPool().getConnection()) {
                    try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                        fillStatement(columns, stmt);
                        try (final ResultSet rs = stmt.executeQuery()) {
                            while (rs.next()) {
                                //noinspection unchecked
                                R rval = (R) rs.getObject(1, selector.valueClass());
                                if (selector.valueClass() == String.class) {
                                    //noinspection unchecked
                                    futureList.add((R) rval.toString().trim());
                                } else if (selector.valueClass() == Boolean.class) {
                                    final Boolean boolVal = rval.toString().trim().equals("1");
                                    //noinspection unchecked
                                    futureList.add((R) boolVal);
                                } else {
                                    futureList.add(rval);
                                }
                            }
                        }
                    }
                }
                returnFuture.complete(futureList);
            } catch (final Exception e) {
                returnFuture.completeExceptionally(e);
            }
        }, config().getThreadPool());
        return returnFuture;
    }

    default void getResult(final Query query, final DatabaseField<R> selector,
                           final CompletableFuture<R> rvalFuture, final PreparedStatement stmt) throws SQLException {
        Objects.nonNull(query);
        Objects.nonNull(selector);
        Objects.nonNull(rvalFuture);
        Objects.nonNull(stmt);
        try (final ResultSet rs = stmt.executeQuery()) {
            if (!rs.next()) {
                throw new SQLException(String.format("Query %s failed, resultset empty", query.getQuery()));
            }
            final R rval = (R) rs.getObject(1, selector.valueClass());
            if (rval == null) {
                rvalFuture.complete(null);
            } else {
                //noinspection unchecked
                if (selector.valueClass() == String.class) {
                    //noinspection unchecked
                    rvalFuture.complete((R) rval.toString().trim());
                } else if (selector.valueClass() == Boolean.class) {
                    final Boolean boolVal = rval.toString().trim().equals("1");
                    //noinspection unchecked
                    rvalFuture.complete((R) boolVal);
                } else {
                    rvalFuture.complete(rval);
                }
            }
        }
    }
}
