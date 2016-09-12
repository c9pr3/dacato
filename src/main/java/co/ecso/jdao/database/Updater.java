package co.ecso.jdao.database;

import co.ecso.jdao.config.ConfigGetter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Updater.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 11.09.16
 */
interface Updater<T> extends StatementFiller, ConfigGetter {

    default CompletableFuture<Boolean> update(final SingleColumnUpdateQuery<T> query) {
        final CompletableFuture<Boolean> returnValueFuture = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try {
                final List<DatabaseField<?>> newArr = new LinkedList<>();
                newArr.addAll(query.values().keySet());
                newArr.add(query.whereColumn());
                final String finalQuery = String.format(query.query(), newArr.toArray());
                try (final Connection c = config().getConnectionPool().getConnection()) {
                    try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                        final List<Object> values = new LinkedList<>();
                        query.values().values().forEach(values::add);
                        values.add(query.whereValue());
                        fillStatement(newArr, values, stmt);
                        returnValueFuture.complete(getResult(stmt));
                    }
                }
            } catch (final Exception e) {
                returnValueFuture.completeExceptionally(e);
            }
        }, config().getThreadPool());
        return returnValueFuture;
    }

    default boolean getResult(final PreparedStatement stmt) throws SQLException {
        stmt.executeUpdate();
        return true;
    }

}
