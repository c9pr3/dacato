package co.ecso.jdao.database.query;

import co.ecso.jdao.database.cache.CacheKey;

import java.util.Objects;

/**
 * SingleColumnQuery.
 *
 * @param <S> SelectType. Type of column to select. P.e. in select first_name from xy this would be String.
 * @param <W> WhereType. Type of column where. P.e. select first_name from ... where id = x this would be Long.
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 09.09.16
 */
public final class SingleColumnQuery<S, W> implements Query<S> {

    /**
     * Query string.
     */
    private final String query;
    /**
     * Column to select.
     */
    private final DatabaseField<S> columnToSelect;
    /**
     * Column where.
     */
    private final DatabaseField<W> columnWhere;
    /**
     * Where value.
     */
    private final W columnWhereValue;

    /**
     * Construct.
     *
     * @param query            Query to execute, p.e. Select %s from table_x where %s = ?
     * @param select           Column to select.
     * @param where            Column where, p.e. "id".
     * @param columnWhereValue Where resultValue, p.e. 10.
     */
    public SingleColumnQuery(final String query, final DatabaseField<S> select, final DatabaseField<W> where,
                             final W columnWhereValue) {
        this.query = query;
        this.columnToSelect = select;
        this.columnWhere = where;
        this.columnWhereValue = columnWhereValue;
    }

    /**
     * Construct.
     *
     * @param query  Query to execute, p.e. Select %s from table_x
     * @param select Column to select.
     */
    public SingleColumnQuery(final String query, final DatabaseField<S> select) {
        this.query = query;
        this.columnToSelect = select;
        this.columnWhere = null;
        this.columnWhereValue = null;
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
    public CacheKey<S> getCacheKey() {
        final DatabaseField<W> where;
        if (columnWhere == null) {
            //noinspection unchecked
            where = new DatabaseField<>("", (Class<W>) Object.class, -1);
        } else {
            where = columnWhere;
        }
        return new CacheKey<>(
                columnToSelect.valueClass(),
                query,
                columnToSelect.name(),
                columnToSelect.valueClass().getName(),
                columnToSelect.sqlType(),
                where.name(),
                where.valueClass().getName(),
                where.sqlType(),
                columnWhereValue);
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
