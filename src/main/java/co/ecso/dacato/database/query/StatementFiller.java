package co.ecso.dacato.database.query;

import co.ecso.dacato.database.querywrapper.DatabaseField;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

/**
 * StatementFiller.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @since 02.09.16
 */
public interface StatementFiller {

    /**
     * Fill statement with columnValuesToSet. Replace all ?.
     *
     * @param columnsWhere Where columns.
     * @param valuesWhere  Where columnValuesToSet.
     * @param stmt         Statement.
     * @param c            Connection.
     * @return Prepared statement.
     * @throws SQLException if fill fails.
     */
    default PreparedStatement fillStatement(final String query, final List<DatabaseField<?>> columnsWhere,
                                            final List<?> valuesWhere, final PreparedStatement stmt,
                                            final Connection c) throws SQLException {
        synchronized (c) {
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
//                System.out.printf("SETTING %s %d to '%s', sqlType: %s%n", columnsWhere.get(i), i + 1, valueToSet,
//                        getTypeByValue(sqlType));
                    if (stmt.isClosed()) {
                        throw new SQLException(String.format("Statement %s closed unexpectedly", stmt.toString()));
                    }
                    stmt.setObject(i + 1, valueToSet, sqlType);
                } catch (final SQLException e) {
                    throw new SQLException(String.format("Could not set '%s' (%s) to '%s' on column '%s', " +
                                    "set to '%s' in query '%s', columnsWhere: '%s', valuesWhere: '%s' : %s",
                            valueToSet.toString(),
                            valueToSet.getClass().getSimpleName(), getTypeByValue(sqlType), columnsWhere.get(i).name(),
                            valuesWhere.get(i), query, Arrays.toString(columnsWhere.toArray()),
                            Arrays.toString(valuesWhere.toArray()), e));
                }
            }
            return stmt;
        }
    }

    default String getTypeByValue(Integer value) {
        final Class<Types> typeClazz = Types.class;
        final Field[] fields = typeClazz.getDeclaredFields();
        for (final Field field : fields) {
            try {
                final int fieldValue = (Integer) field.get(Integer.class);
                if (value == fieldValue) {
                    return field.getName();
                }
            } catch (final IllegalAccessException ignored) {
                //
            }
        }
        return "unknownType";
    }
}
