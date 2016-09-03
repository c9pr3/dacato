package co.ecso.jdao.database;

import co.ecso.jdao.config.ConfigGetter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * MultipleColumnFinder.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 28.08.16
 */
interface MultipleColumnFinder extends ConfigGetter, StatementFiller {

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
        final Map<DatabaseField<?>, CompletableFuture<?>> whereFuture = query.columnsWhere();

        final CompletableFuture<List<List<?>>> returnValueFuture = new CompletableFuture<>();

        final List<Object> format = new ArrayList<>();
        final List<?> whereList = whereFuture.values().stream().map(CompletableFuture::join)
                .collect(Collectors.toList());
        CompletableFuture.runAsync(() -> {
            format.addAll(columnsToSelect);
            format.addAll(whereFuture.keySet());
            //find a way to find out if format.toArray has the right amount of entries needed to solve query.query()
            final String finalQuery = String.format(query.query(), format.toArray());
            try (Connection c = config().getConnectionPool().getConnection()) {
                try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                    returnValueFuture.complete(getResult(finalQuery, columnsToSelect,
                            fillStatement(new ArrayList<>(whereFuture.keySet()), whereList, stmt)));
                }
            } catch (final Exception e) {
                returnValueFuture.completeExceptionally(e);
            }
        }, config().getThreadPool());
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
