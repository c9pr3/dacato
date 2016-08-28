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
 * CLASS
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 28.08.16
 */
public interface Finder<R, T> {

    default R findOne(final Query query, final DatabaseField<T> column, final CompletableFuture<?> whereIdFuture) {
        final CompletableFuture<Object> f = new CompletableFuture<>();
        CompletableFuture<Long> wheref = whereIdFuture.handle((ok, ex) -> {
            if (ex != null) {
                f.completeExceptionally(ex);
                return null;
            } else {
                return Long.valueOf(ok.toString().trim());
            }
        });
        if (wheref.isCompletedExceptionally()) {
            return (R)wheref;
        }
        wheref.thenAccept(whereId -> {
            try {
                final String finalQuery = String.format(query.getQuery(), column);
                try (Connection c = this.getConfig().getConnectionPool().getConnection()) {
                    try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                        stmt.setObject(1, whereId, column.sqlType());
                        getResult(query, column, f, stmt);
                    }
                }
            } catch (final Exception e) {
                e.printStackTrace();
                f.completeExceptionally(e);
            }
        });
        return (R)f;
    }

    default R findOne(final Query query, final DatabaseField<T> column, final Map<DatabaseField<?>, ?> columns) {
        final CompletableFuture<Object> f = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try (Connection c = this.getConfig().getConnectionPool().getConnection()) {
                final List<DatabaseField> newArr = new LinkedList<>();
                newArr.add(column);
                newArr.addAll(columns.keySet());
                final String finalQuery = String.format(query.getQuery(), newArr.toArray());
                try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                    for (int i = 1; i <= columns.size(); i++) {
                        final int sqlType = ((DatabaseField) columns.keySet().toArray()[i - 1]).sqlType();
                        final Object valueToSet = columns.values().toArray()[i - 1];
                        stmt.setObject(i, valueToSet, sqlType);
                    }
                    getResult(query, column, f, stmt);
                }
            } catch (final Exception e) {
                e.printStackTrace();
                f.completeExceptionally(e);
            }
        }, getThreadPool());
        return (R)f;
    }

    default CompletableFuture<LinkedList<R>> findMany(final Query query, final Map<DatabaseField<?>, ?> columns) {
        final CompletableFuture<LinkedList<R>> f = new CompletableFuture<>();
        final LinkedList<R> futureList = new LinkedList<>();
        CompletableFuture.runAsync(() -> {
            try {
                final String finalQuery = String.format(query.getQuery(), columns.keySet().toArray());
                try (final Connection c = this.getConfig().getConnectionPool().getConnection()) {
                    try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                        for (int i = 1; i <= columns.size(); i++) {
                            final int sqlType = ((DatabaseField) columns.keySet().toArray()[i - 1]).sqlType();
                            final Object valueToSet = columns.values().toArray()[i - 1];
                            stmt.setObject(i, valueToSet, sqlType);
                        }
                        try (final ResultSet rs = stmt.executeQuery()) {
                            while (rs.next()) {
                                futureList.add((R)rs.getObject("id"));
                            }
                        }
                    }
                }
                f.complete(futureList);
            } catch (final Exception e) {
                e.printStackTrace();
                f.completeExceptionally(e);
            }
        }, getThreadPool());
        return f;
    }

    ApplicationConfig getConfig();

    default ScheduledExecutorService getThreadPool() {
        ScheduledExecutorService threadPool = this.getConfig().getThreadPool();
        if (this.getConfig().getThreadPool() == null) {
            threadPool = new ScheduledThreadPoolExecutor(getConfig().getMysqlMaxPool());
        }
        return threadPool;
    }

    default void getResult(final Query query, final DatabaseField<?> column,
                           final CompletableFuture<Object> f, final PreparedStatement stmt) throws SQLException {
        Objects.nonNull(query);
        Objects.nonNull(column);
        Objects.nonNull(f);
        Objects.nonNull(stmt);
        try (final ResultSet rs = stmt.executeQuery()) {
            if (!rs.next()) {
                throw new SQLException(String.format("Query %s failed, resultset empty", query.getQuery()));
            }
            final Object rval = rs.getObject(column.toString(), column.valueClass());
            if (rval == null) {
                throw new SQLException(String.format("Result for %s, %s was null",
                        column.toString(), query.getQuery()));
            } else {
                if ("String".equals(column.valueClass().getSimpleName())) {
                    f.complete(rval.toString().trim());
                } else {
                    f.complete(rval);
                }
            }
        }
    }
}
