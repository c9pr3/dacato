package co.ecso.jdao;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;

/**
 * DatabaseConnection.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 16.03.16
 */
public interface DatabaseConnection {

    @SuppressWarnings("Duplicates")
    default CompletableFuture<?> findOne(final Query query, final DatabaseField<?> column, final Map<DatabaseField<?>, ?> columns) {
        final CompletableFuture<Object> f = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try (Connection c = this.pooledConnection()) {
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
            } catch (final Exception e) {
                e.printStackTrace();
                f.completeExceptionally(e);
            }
        });
        return f;
    }

    default CompletableFuture<?> findOne(final Query query, final DatabaseField<?> column, final CompletableFuture<?> whereIdFuture) {
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
            return wheref;
        }
        wheref.thenAccept(whereId -> {
            try {
                final String finalQuery = String.format(query.getQuery(), column);
                try (Connection c = this.pooledConnection()) {
                    try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                        stmt.setObject(1, whereId, column.sqlType());
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
            } catch (final Exception e) {
                e.printStackTrace();
                f.completeExceptionally(e);
            }
        });
        return f;
    }

    default CompletableFuture<LinkedList<?>> findMany(final Query query, final Map<DatabaseField<?>, ?> columns) {
        final CompletableFuture<LinkedList<?>> f = new CompletableFuture<>();
        final LinkedList<Long> futureList = new LinkedList<>();
        CompletableFuture.runAsync(() -> {
            try {
                final String finalQuery = String.format(query.getQuery(), columns.keySet().toArray());
                try (final Connection c = this.pooledConnection()) {
                    try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                        for (int i = 1; i <= columns.size(); i++) {
                            final int sqlType = ((DatabaseField) columns.keySet().toArray()[i - 1]).sqlType();
                            final Object valueToSet = columns.values().toArray()[i - 1];
                            stmt.setObject(i, valueToSet, sqlType);
                        }
                        try (final ResultSet rs = stmt.executeQuery()) {
                            while (rs.next()) {
                                futureList.add(rs.getLong("id"));
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

    default CompletableFuture<Boolean> truncate(final Query query) {
        final CompletableFuture<Boolean> f = new CompletableFuture<>();
        try (final Connection c = pooledConnection()) {
            try (final PreparedStatement stmt = c.prepareStatement(query.getQuery())) {
                final boolean res = stmt.execute();
                f.complete(res);
            }
        } catch (final Exception e) {
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
            System.out.println("INSERT START ");
            try {
                try (final Connection c = pooledConnection()) {
                    try (final PreparedStatement stmt = c.prepareStatement(query.getQuery(), Statement.RETURN_GENERATED_KEYS)) {
                        int i = 1;
                        for (final DatabaseField<?> databaseField : values.keySet()) {
                            System.out.println("SETTING " + values.get(databaseField) + " to type " + databaseField.sqlType());
                            stmt.setObject(i, values.get(databaseField), databaseField.sqlType());
                            i++;
                        }
                        stmt.executeUpdate();
                        try (final ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                            if (!generatedKeys.next()) {
                                throw new SQLException(String.format("Query %s failed, resultset empty", query.getQuery()));
                            }
                            int sleep = ThreadLocalRandom.current().nextInt(1000, 5000 + 1);

                            System.out.println("INSERT COMPLETED NORMAL - sleeping for " + sleep);
                            Thread.sleep(sleep);
                            f.complete(generatedKeys.getLong(1));
                        }
                    }
                }
            } catch (final Exception e) {
                e.printStackTrace();
                System.out.println("INSERT COMPLETED EXCEPTIONALLY");
                f.completeExceptionally(e);
            }
            System.out.println("INSERT END");
        }, getThreadPool());
        return f;
    }

//    static Class<?> toClass(int type) {
//        Class<?> result = Object.class;
//        switch (type) {
//            case Types.CHAR:
//            case Types.VARCHAR:
//            case Types.LONGVARCHAR:
//                result = String.class;
//                break;
//
//            case Types.NUMERIC:
//            case Types.DECIMAL:
//                result = java.math.BigDecimal.class;
//                break;
//
//            case Types.BIT:
//                result = Boolean.class;
//                break;
//
//            case Types.TINYINT:
//                result = Byte.class;
//                break;
//
//            case Types.SMALLINT:
//                result = Short.class;
//                break;
//
//            case Types.INTEGER:
//                result = Integer.class;
//                break;
//
//            case Types.BIGINT:
//                result = Long.class;
//                break;
//
//            case Types.REAL:
//            case Types.FLOAT:
//                result = Float.class;
//                break;
//
//            case Types.DOUBLE:
//                result = Double.class;
//                break;
//
//            case Types.BINARY:
//            case Types.VARBINARY:
//            case Types.LONGVARBINARY:
//                result = Byte[].class;
//                break;
//
//            case Types.DATE:
//                result = java.sql.Date.class;
//                break;
//
//            case Types.TIME:
//                result = java.sql.Time.class;
//                break;
//
//            case Types.TIMESTAMP:
//                result = java.sql.Timestamp.class;
//                break;
//        }
//
//        return result;
//    }

//    CompletableFuture<?> selectIdWithValues(final String tableName, final Map<DatabaseField<?>, ?> values);

//    CompletableFuture<List<Long>> selectIdsWithValues(final String tableName, final Map<DatabaseField<?>, ?> values);

//    CompletableFuture<Boolean> update(final String tableName, final Map<DatabaseField<?>, ? extends Comparable> map,
//                                      final CompletableFuture<?> whereId);

}
