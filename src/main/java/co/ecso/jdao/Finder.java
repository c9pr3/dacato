package co.ecso.jdao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Finder.
 *
 * @param <R> ReturnValue, p.e. Long or String
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 28.08.16
 */
public interface Finder<R> extends ConfigFinder {

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
                e.printStackTrace();
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
                    for (int i = 1; i <= columnsToSelect.size(); i++) {
                        final int sqlType = ((DatabaseField) columnsToSelect.keySet().toArray()[i - 1]).sqlType();
                        final Object valueToSet = columnsToSelect.values().toArray()[i - 1];
                        stmt.setObject(i, valueToSet, sqlType);
                    }
                    getResult(query, columnToReturn, f, stmt);
                }
            } catch (final Exception e) {
                e.printStackTrace();
                f.completeExceptionally(e);
            }
        }, getThreadPool());
        return f;
    }

    default CompletableFuture<LinkedList<R>> findMany(final Query query, DatabaseField<?> selector,
                                                      final Map<DatabaseField<?>, ?> columns) {

        System.out.println("FIND MANY");
        final CompletableFuture<LinkedList<R>> returnFuture = new CompletableFuture<>();
        final LinkedList<R> futureList = new LinkedList<>();
        CompletableFuture.runAsync(() -> {
            try {
                LinkedList<DatabaseField<?>> newColumns = new LinkedList<>();
                newColumns.add(selector);
                newColumns.addAll(columns.keySet());
                final String finalQuery = String.format(query.getQuery(), newColumns.toArray());
                System.out.println("FIND MANY " + finalQuery);
                try (Connection c = config().getConnectionPool().getConnection()) {
                    System.out.println("GOT CONNECTION");
                    try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                        System.out.println("GOT STATEMENT");
                        for (int i = 1; i <= columns.size(); i++) {
                            System.out.println("STMT FILL UP " + i);
                            final int sqlType = ((DatabaseField) columns.keySet().toArray()[i - 1]).sqlType();
                            final Object valueToSet = columns.values().toArray()[i - 1];
                            stmt.setObject(i, valueToSet, sqlType);
                        }
                        try (final ResultSet rs = stmt.executeQuery()) {
                            System.out.println("RESULT");
                            while (rs.next()) {
                                System.out.println("R.next");
                                //noinspection unchecked
                                R retVal = (R) rs.getObject(1, selector.valueClass());
                                //System.out.println("RETVAL IS OF TYPE " + retVal.getClass().getSimpleName());
                                futureList.add(retVal);
                            }
                        }
                    }
                }
                System.out.println("returnFuture complete");
                returnFuture.complete(futureList);
            } catch (final Exception e) {
                e.printStackTrace();
                System.out.println("returnFuture complete EXCEPTION");
                returnFuture.completeExceptionally(e);
            }
        }, getThreadPool());
        return returnFuture;
    }

    default ScheduledExecutorService getThreadPool() {
        ScheduledExecutorService threadPool = config().getThreadPool();
        if (config().getThreadPool() == null) {
            threadPool = new ScheduledThreadPoolExecutor(config().getMysqlMaxPool());
        }
        return threadPool;
    }

    default void getResult(final Query query, final DatabaseField<R> column,
                           final CompletableFuture<R> returnValue, final PreparedStatement stmt) throws SQLException {
        Objects.nonNull(query);
        Objects.nonNull(column);
        Objects.nonNull(returnValue);
        Objects.nonNull(stmt);
        try (final ResultSet rs = stmt.executeQuery()) {
            if (!rs.next()) {
                throw new SQLException(String.format("Query %s failed, resultset empty", query.getQuery()));
            }
            final R rval = (R) rs.getObject(column.toString().trim(), column.valueClass());
            if (rval == null) {
                throw new SQLException(String.format("Result for %s, %s was null",
                        column.toString(), query.getQuery()));
            } else {
                //noinspection unchecked
                returnValue.complete((R)rval.toString().trim());
//                if ("String".equals(column.valueClass().getSimpleName())) {
//                    returnValue.complete(rval.toString().trim());
//                } else {
//                    returnValue.complete(rval);
//                }
            }
        }
    }
}
