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
@SuppressWarnings("WeakerAccess")
public interface StatementFiller {

    /**
     * Fill statement with columnValuesToSet. Replace all ?.
     *
     * @param columnsWhere Where columns.
     * @param valuesWhere Where columnValuesToSet.
     * @param stmt Statement.
     * @return Prepared statement.
     * @throws SQLException if fill fails.
     */
    default PreparedStatement fillStatement(final String query, final List<DatabaseField<?>> columnsWhere,
                                            final List<?> valuesWhere,
                                            final PreparedStatement stmt) throws SQLException {
        //we *need* to have the same amount
        // +1 for the auto_increment "null, [...]"
        int questionCount = 0;
        for (int i = 0; i < query.length(); i++) {
            if (query.charAt(i) == '?') {
                questionCount++;
            }
        }
        if (columnsWhere.size() > questionCount + 1) {
            throw new SQLException(String.format("Found %d '?' but %d to replace them in query %s.",
                    questionCount, columnsWhere.size(), query));
        }
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
