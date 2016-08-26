package co.ecso.jdao.mysql;

import co.ecso.jdao.ApplicationConfig;
import co.ecso.jdao.ConnectionPool;
import co.ecso.jdao.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MysqlConnection.
 * Decorator for static JDBCConnector.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 16.03.16
 */
public final class MysqlConnection implements DatabaseConnection {
    private static final Map<Integer, ConnectionPool> CONNECTION_POOL_MAP = new ConcurrentHashMap<>();
    private ApplicationConfig config;

    public MysqlConnection(final ApplicationConfig config) {
        this.config = config;
        if (!CONNECTION_POOL_MAP.containsKey(config.hashCode())) {
            CONNECTION_POOL_MAP.putIfAbsent(config.hashCode(), config.getConnectionPool());
        }
    }

    @Override
    public Connection pooledConnection() throws SQLException {
        return CONNECTION_POOL_MAP.get(config.hashCode()).getConnection();
    }

    @Override
    public ApplicationConfig getConfig() {
        return this.config;
    }

//    @Override
//    public CompletableFuture<Long> selectIdWithValues(final String tableName,
//                                                            final Map<DatabaseField<?>, ?> values) {
//        final StringBuilder sql = new StringBuilder();
//        values.forEach((key, value) -> sql.append(key).append("= '").append(value).append("' AND "));
//        final CompletableFuture<Long> f = new CompletableFuture<>();
//        CompletableFuture.runAsync(() -> {
//            try {
//                try (final Connection c = this.pooledConnection()) {
//                    try (final Statement stmt = c.createStatement()) {
//                        final String query = String.format("SELECT id FROM %s WHERE %s",
//                                tableName, sql.substring(0, sql.length() - 5));
//                        try (final ResultSet rs = stmt.executeQuery(query)) {
//                            if (!rs.next()) {
//                                throw new SQLException(String.format("Query %s failed, resultset empty", sql));
//                            }
//                            f.complete(rs.getLong("id"));
//                        }
//                    }
//                }
//            } catch (final SQLException e) {
//                f.completeExceptionally(e);
//            }
//        }, getThreadPool());
//        return f;
//    }
//
//    @Override
//    public CompletableFuture<List<Long>> selectIdsWithValues(final String tableName,
//                                                                   final Map<DatabaseField<?>, ?> values) {
//        final StringBuilder sql = new StringBuilder();
//        values.forEach((key, value) -> sql.append(key).append("= '").append(value).append("' AND "));
//        final CompletableFuture<List<Long>> f = new CompletableFuture<>();
//        CompletableFuture.runAsync(() -> {
//            final List<Long> futureList = new ArrayList<>();
//            try {
//                try (final Connection c = this.pooledConnection()) {
//                    try (final Statement stmt = c.createStatement()) {
//                        final String query = String.format("SELECT id FROM %s WHERE %s",
//                                tableName, sql.substring(0, sql.length() - 5));
//                        try (final ResultSet rs = stmt.executeQuery(query)) {
//                            while (rs.next()) {
//                                futureList.add(rs.getLong("id"));
//                            }
//                        }
//                    }
//                }
//                f.complete(futureList);
//            } catch (final SQLException e) {
//                f.completeExceptionally(e);
//            }
//        }, getThreadPool());
//        return f;
//    }

//    @Override
//    public CompletableFuture<Boolean> update(final String tableName,
//                                             final Map<DatabaseField<?>, ? extends Comparable> values,
//                                             final CompletableFuture<?> whereId) {
//        final StringBuilder sql = new StringBuilder();
//        values.forEach((key, value) -> sql.append(key.toString()).append("= '").append(value).append("', "));
//        final CompletableFuture<Boolean> f = new CompletableFuture<>();
//        whereId.thenAcceptAsync(where -> {
//            try {
//                try (final Connection c = this.pooledConnection()) {
//                    try (final Statement stmt = c.createStatement()) {
//                        final String query = String.format("UPDATE %s SET %s WHERE id=%s",
//                                tableName, sql.substring(0, sql.length() - 2), where);
//                        f.complete(stmt.execute(query));
//                    }
//                }
//            } catch (final SQLException e) {
//                f.completeExceptionally(e);
//            }
//        }, getThreadPool());
//        return f;
//    }
}
