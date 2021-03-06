package co.ecso.dacato.database.querywrapper;

import java.util.Arrays;
import java.util.Objects;

/**
 * SingleColumnQuery.
 *
 * @param <S> SelectType. Type of column to select. P.e. in select first_name from xy this would be String.
 * @param <W> WhereType. Type of column where. P.e. select first_name from ... where id = x this would be Long.
 * @author Christian Scharmach (cs@e-cs.co)
 * @since 09.09.16
 */
public final class SingleColumnQuery<S, W> implements Query<S> {

    private static final long serialVersionUID = 5941571185193135160L;
    private final String query;
    private final DatabaseField<S> columnToSelect;
    private final DatabaseField<W> columnWhere;
    private final W columnWhereValue;
    private final String tableName;

    /**
     * Construct.
     *
     * @param query            Query to execute, p.e. Select %s from table_x where %s = ?
     * @param select           Column to select.
     * @param where            Column where, p.e. "id".
     * @param columnWhereValue Where resultValue, p.e. 10.
     */
    public SingleColumnQuery(final String tableName, final String query, final DatabaseField<S> select, final DatabaseField<W> where,
                             final W columnWhereValue) {
        this.query = query;
        this.columnToSelect = select;
        this.columnWhere = where;
        this.columnWhereValue = columnWhereValue;
        this.tableName = tableName;
    }

    /**
     * Construct.
     *
     * @param query  Query to execute, p.e. Select %s from table_x
     * @param select Column to select.
     */
    public SingleColumnQuery(final String tableName, final String query, final DatabaseField<S> select) {
        this.query = query;
        this.columnToSelect = select;
        this.columnWhere = null;
        this.columnWhereValue = null;
        this.tableName = tableName;
    }

    /**
     * Column to select.
     *
     * @return Column to select.
     */
    public DatabaseField<S> columnToSelect() {
        // we *always* need to select
        Objects.requireNonNull(columnToSelect);
        return columnToSelect;
    }

    /**
     * Column where.
     *
     * @return Column where.
     */
    public DatabaseField<W> columnWhere() {
        // we MAY have a where column
        return columnWhere;
    }

    @Override
    public String query() {
        return query;
    }

    @Override
    public String tableName() {
        return tableName;
    }

    @Override
    public Class<S> queryType() {
        return columnToSelect.valueClass();
    }

    @Override
    public String toString() {
        final Class<?> whereClass;
        final String whereName;
        final int whereSqlType;
        if (columnWhere == null) {
            whereClass = Object.class;
            whereName = Object.class.getName();
            whereSqlType = -1;
        } else {
            whereClass = columnWhere.valueClass();
            whereName = columnWhere.valueClass().getName();
            whereSqlType = columnWhere.sqlType();
        }
        return String.valueOf(Arrays.asList(
                columnToSelect.valueClass(),
                query,
                columnToSelect.name(),
                columnToSelect.valueClass().getName(),
                columnToSelect.sqlType(),
                whereName,
                whereClass,
                whereSqlType,
                columnWhereValue
        ));
    }

    /**
     * Where resultValue.
     *
     * @return Where resultValue.
     */
    public W columnWhereValue() {
        return this.columnWhereValue;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SingleColumnQuery<?, ?> that = (SingleColumnQuery<?, ?>) o;
        return Objects.equals(query, that.query) &&
                Objects.equals(columnToSelect, that.columnToSelect) &&
                Objects.equals(columnWhere, that.columnWhere) &&
                Objects.equals(columnWhereValue, that.columnWhereValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, columnToSelect, columnWhere, columnWhereValue);
    }

}
