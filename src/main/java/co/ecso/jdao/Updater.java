package co.ecso.jdao;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Updater.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 30.08.16
 */
@SuppressWarnings("Duplicates")
public interface Updater extends ConfigGetter {

    default CompletableFuture<Boolean> update(final Query query, final LinkedHashMap<DatabaseField<?>, ?> values,
                                              final CompletableFuture<?> whereFuture) {
        final CompletableFuture<Boolean> returnValueFuture = new CompletableFuture<>();
        whereFuture.thenAccept(whereId -> {
            try {
                final List<Object> newArr = new LinkedList<>();
                values.keySet().forEach(k -> newArr.add(k.toString()));
                newArr.add(whereId);
                final String finalQuery = String.format(query.getQuery(), newArr.toArray());
                try (Connection c = config().getConnectionPool().getConnection()) {
                    try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                        fillStatement(values, whereId, stmt);
                        getResult(query, returnValueFuture, stmt);
                    }
                }
            } catch (final Exception e) {
                returnValueFuture.completeExceptionally(e);
            }
        });
        return returnValueFuture;
    }

    default void getResult(final Query query, final CompletableFuture<Boolean> retValFuture,
                           final PreparedStatement stmt)
            throws SQLException {
        Objects.nonNull(query);
        Objects.nonNull(retValFuture);
        Objects.nonNull(stmt);
        stmt.executeUpdate();
        retValFuture.complete(true);
    }

    default void fillStatement(final LinkedHashMap<DatabaseField<?>, ?> values, final Object whereId,
                               final PreparedStatement stmt) throws SQLException {
        int i = 1;
        for (final DatabaseField<?> databaseField : values.keySet()) {
            try {
                if (values.get(databaseField) == null) {
                    stmt.setNull(i, databaseField.sqlType());
                } else {
                    stmt.setObject(i, values.get(databaseField), databaseField.sqlType());
                }
            } catch (final SQLDataException | SQLSyntaxErrorException e) {
                throw new SQLException(String.format("Could not set %s to %d: %s",
                        values.get(databaseField), databaseField.sqlType(), e));
            }
            i++;
        }
        stmt.setObject(i, whereId);
    }
}
