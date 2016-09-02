package co.ecso.jdao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * MultipleReturnFinder.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 28.08.16
 */
public interface MultipleReturnFinder extends ConfigGetter, StatementFiller {

    /**
     * It is List<List<?>> because we have an unknown amount of rows for an unknown amount of select-attributes.
     * For instance:
     *  rows:
     *      0: id, first_name, foo
     *      1: id, first_name, foo
     *      2: id, first_name, foo
     *      etc.
     *
     * @param query MultipleFindQuery to do.
     * @return List of list of values which were found.
     */
    default CompletableFuture<List<List<?>>> find(final MultipleFindQuery query) {
        final List<DatabaseField<?>> columnsToSelect = query.columnsToSelect();
        final List<DatabaseField<?>> columnsWhere = query.columnsWhere();
        final CompletableFuture<?> whereFuture = query.whereFuture();

        final CompletableFuture<List<List<?>>> returnValueFuture = new CompletableFuture<>();

        final List<Object> format = new ArrayList<>();
        whereFuture.thenAccept(whereColumn -> {
            format.addAll(columnsToSelect);
            format.addAll(columnsWhere);
            //find a way to find out if format.toArray has the right amount of entries needed to solve query.query()
            final String finalQuery = String.format(query.query(), format.toArray());
            try (Connection c = config().getConnectionPool().getConnection()) {
                try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                    returnValueFuture.complete(getResult(finalQuery, columnsToSelect,
                            fillStatement(finalQuery, columnsWhere, whereColumn, stmt)));
                }
            } catch (final Exception e) {
                returnValueFuture.completeExceptionally(e);
            }
        });
        return returnValueFuture;
    }

    //* @todo map back to DatabaseField with value rather than types.
    default List<List<?>> getResult(final String finalQuery, final List<DatabaseField<?>> columnsToSelect,
                              final PreparedStatement stmt) throws SQLException {
        final List<List<?>> rvalList = new LinkedList<>();
        try (final ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                List<Object> thisList = new LinkedList<>();
                for (int i = 0; i < columnsToSelect.size(); i++) {
                    final DatabaseField<?> selector = columnsToSelect.get(i);
                    final Object rval = rs.getObject(i + 1, selector.valueClass());
                    if (rval == null) {
                        rvalList.add(null);
                    } else {
                        if (selector.valueClass() == String.class) {
                            //noinspection unchecked
                            thisList.add(rval.toString().trim());
                        } else if (selector.valueClass() == Boolean.class) {
                            final Boolean boolVal = rval.toString().trim().equals("1");
                            //noinspection unchecked
                            thisList.add(boolVal);
                        } else {
                            thisList.add(rval);
                        }
                    }
                }
                rvalList.add(thisList);
            }
        }
        return rvalList;
    }

}
