package co.ecso.jdao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * MultipleReturnFinder.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 30.08.16
 */
public interface MultipleReturnFinder<Void> extends Finder<Void> {

    default CompletableFuture<LinkedList<?>> findeOne(final Query query,
                                                      final LinkedList<DatabaseField<?>> columnsToReturn,
                                                      final LinkedHashMap<DatabaseField<?>, ?> columnsToSelect) {
        final CompletableFuture<LinkedList<?>> rval = new CompletableFuture<>();

        final LinkedList<Object> newArr = new LinkedList<>();
        newArr.addAll(columnsToReturn);
        columnsToSelect.forEach((k, v) -> newArr.add(k));
        final String finalQuery = String.format(query.getQuery(), newArr.toArray());

        CompletableFuture.runAsync(() -> {
            try (Connection c = config().getConnectionPool().getConnection()) {
                try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                    fillStatement(columnsToSelect, stmt);
                    getListResult(columnsToReturn, rval, stmt);
                }
            } catch (final Exception e) {
                rval.completeExceptionally(e);
            }
        }, config().getThreadPool());

        return rval;
    }

    default CompletableFuture<LinkedList<LinkedList<?>>> findeMany(final Query query,
                                                       final LinkedList<DatabaseField<?>> columnsToReturn,
                                                       final LinkedHashMap<DatabaseField<?>, ?> columnsToSelect) {
        final CompletableFuture<LinkedList<LinkedList<?>>> rval = new CompletableFuture<>();

        return rval;
    }

    default void getListResult(final LinkedList<DatabaseField<?>> columnsToReturn,
                               final CompletableFuture<LinkedList<?>> rvalFuture, final PreparedStatement stmt)
            throws SQLException {
        Objects.nonNull(rvalFuture);
        Objects.nonNull(stmt);
        final LinkedList<Object> newList = new LinkedList<>();
        try (final ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                for (int i = 0; i < columnsToReturn.size(); i++) {
                    final Object rval = rs.getObject(i + 1, columnsToReturn.get(i).valueClass());
                    if (rval == null) {
                        newList.add(i, null);
                    } else {
                        if (columnsToReturn.get(i).valueClass() == String.class) {
                            newList.add(i, rval.toString().trim());
                        } else if (columnsToReturn.get(i).valueClass() == Boolean.class) {
                            final Boolean boolVal = rval.toString().trim().equals("1");
                            //noinspection unchecked
                            newList.add(i, boolVal);
                        } else {
                            newList.add(i, rval);
                        }
                    }
                }
            }
        }
        rvalFuture.complete(newList);
    }

}
