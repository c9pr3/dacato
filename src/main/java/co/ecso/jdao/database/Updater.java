package co.ecso.jdao.database;

import co.ecso.jdao.config.ConfigGetter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Updater.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 30.08.16
 */
interface Updater extends ConfigGetter, StatementFiller {

    default CompletableFuture<Boolean> update(final String query, final Map<DatabaseField<?>, ?> valuesToSet,
                                              final Map<DatabaseField<?>, ?> whereMap) {
        final CompletableFuture<Boolean> returnValueFuture = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try {
                final List<DatabaseField<?>> newArr = new LinkedList<>();
                final List<String> selectFormat = new ArrayList<>();
                final List<String> whereFormat = new ArrayList<>();
                valuesToSet.forEach((k, v) -> selectFormat.add("%s = ?"));
                whereMap.forEach((k, v) -> whereFormat.add("%s = ?"));
                valuesToSet.keySet().forEach(newArr::add);
                whereMap.keySet().forEach(newArr::add);
                final String finalQuery = String.format(
                        String.format(query, selectFormat.stream().collect(Collectors.joining(", ")),
                                whereFormat.stream().collect(Collectors.joining(", "))), newArr.toArray());
                try (final Connection c = config().getConnectionPool().getConnection()) {
                    try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                        final List<Object> values = new LinkedList<>();
                        valuesToSet.values().forEach(values::add);
                        whereMap.values().forEach(values::add);
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
