package co.ecso.jdao.database.internals;

import co.ecso.jdao.database.query.DatabaseField;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * StatementFiller.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 02.09.16
 */
public interface StatementFiller {

    default PreparedStatement fillStatement(final List<DatabaseField<?>> columnsWhere, final List<?> valuesWhere,
                                            final PreparedStatement stmt) throws SQLException {
        for (int i = 0; i < valuesWhere.size(); i++) {
            final Object valueToSet = valuesWhere.get(i);
            if (columnsWhere.get(i) == null) {
                // it may happen if no where is set
                continue;
            }
            final int sqlType = columnsWhere.get(i).sqlType();
            try {
                stmt.setObject(i + 1, valueToSet, sqlType);
            } catch (final SQLException e) {
                throw new SQLException(String.format("Could not set %s (%s) to %d: %s", valueToSet,
                        valueToSet.getClass().getSimpleName(), sqlType, e));
            }
        }
        return stmt;
    }

}
