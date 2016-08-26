package co.ecso.jdao;

import java.sql.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * DatabaseConnection.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 16.03.16
 */
public interface DatabaseConnection {

    default CompletableFuture<?> findOne(final Query query, final CompletableFuture<?> whereId,
                                         final DatabaseField<?> column) throws SQLException {
        final CompletableFuture<Object> f = new CompletableFuture<>();
        whereId.handle((ok, ex) -> Long.valueOf(ok.toString().trim()))
                .thenAccept(w -> {
                    try {
                        try (Connection c = this.pooledConnection()) {
                            try (final PreparedStatement stmt = c.prepareStatement(query.getQuery())) {
                                stmt.setLong(1, w);
                                try (final ResultSet rs = stmt.executeQuery()) {
                                    if (!rs.next()) {
                                        throw new SQLException(String.format("Query %s failed, resultset empty", query.getQuery()));
                                    }
                                    final Object rval = rs.getObject(column.toString(), column.valueClass());
                                    if (rval == null) {
                                        throw new SQLException(String.format("No result for query %s", query.getQuery()));
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
                    } catch (final SQLException e) {
                        e.printStackTrace();
                        f.completeExceptionally(e);
                    }
                });
        return f;
    }

    default CompletableFuture<ConcurrentLinkedQueue<Long>> findMany(final Query query) {
        final CompletableFuture<ConcurrentLinkedQueue<Long>> f = new CompletableFuture<>();
        final ConcurrentLinkedQueue<Long> futureList = new ConcurrentLinkedQueue<>();
        CompletableFuture.runAsync(() -> {
            try {
                try (final Connection c = this.pooledConnection()) {
                    try (final Statement stmt = c.createStatement()) {
                        try (final ResultSet rs = stmt.executeQuery(query.getQuery())) {
                            while (rs.next()) {
                                futureList.add(rs.getLong("id"));
                            }
                        }
                    }
                }
                f.complete(futureList);
            } catch (final SQLException e) {
                e.printStackTrace();
                f.completeExceptionally(e);
            }
        }, getThreadPool());
        return f;
    }

    default CompletableFuture<Boolean> truncate(final Query query) {
        final CompletableFuture<Boolean> f = new CompletableFuture<>();
            try (final Connection c = pooledConnection()) {
                try (final PreparedStatement stmt = c.prepareStatement(query.getQuery())) {
                    final boolean res = stmt.execute();
                    f.complete(res);
                }
            } catch (final SQLException e) {
                e.printStackTrace();
                f.completeExceptionally(e);
            }
        return f;
    }

    default ScheduledExecutorService getThreadPool() {
        ScheduledExecutorService threadPool = this.getConfig().getThreadPool();
        if (this.getConfig().getThreadPool() == null) {
            threadPool = new ScheduledThreadPoolExecutor(getConfig().getMysqlMaxPool());
        }
        return threadPool;
    }

    /**
     * Get Java SQL Connection.
     *
     * @return Connection.
     * @throws SQLException if SQL fails.
     */
    Connection pooledConnection() throws SQLException;

    ApplicationConfig getConfig();

    default CompletableFuture<Long> insert(final Query query, final Map<DatabaseField<?>, ?> values) {
        final CompletableFuture<Long> f = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try {
                try (final Connection c = pooledConnection()) {
                    try (final PreparedStatement stmt = c.prepareStatement(query.getQuery(), Statement.RETURN_GENERATED_KEYS)) {
                        int i = 1;
                        for (final DatabaseField<?> databaseField : values.keySet()) {
                            stmt.setObject(i, values.get(databaseField), databaseField.sqlType());
                            i++;
                        }
                        stmt.executeUpdate();
                        try (final ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                            if (!generatedKeys.next()) {
                                throw new SQLException(String.format("Query %s failed, resultset empty", query.getQuery()));
                            }
                            f.complete(generatedKeys.getLong(1));
                        }
                    }
                }
            } catch (final SQLException e) {
                f.completeExceptionally(e);
            }
        }, getThreadPool());
        return f;
    }

//    CompletableFuture<?> selectIdWithValues(final String tableName, final Map<DatabaseField<?>, ?> values);

//    CompletableFuture<List<Long>> selectIdsWithValues(final String tableName, final Map<DatabaseField<?>, ?> values);

//    CompletableFuture<Boolean> update(final String tableName, final Map<DatabaseField<?>, ? extends Comparable> map,
//                                      final CompletableFuture<?> whereId);


}
