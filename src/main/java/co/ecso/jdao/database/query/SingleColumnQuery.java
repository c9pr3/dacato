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
@SuppressWarnings("WeakerAccess")
public final class SingleColumnQuery<S, W> implements Query<S> {

    private final String query;
    private final DatabaseField<S> columnToSelect;
    private final DatabaseField<W> columnWhere;
    private final W columnWhereValue;

    public SingleColumnQuery(final String query, final DatabaseField<S> select, final DatabaseField<W> where,
                             final W columnWhereValue) {
        this.query = query;
        this.columnToSelect = select;
        this.columnWhere = where;
        this.columnWhereValue = columnWhereValue;
    }

    public SingleColumnQuery(final String query, final DatabaseField<S> select, final DatabaseField<W> where) {
        this.query = query;
        this.columnToSelect = select;
        this.columnWhere = where;
        this.columnWhereValue = null;
    }

    public SingleColumnQuery(final String query, final DatabaseField<S> select) {
       this(query, select, null);
    }

    public DatabaseField<S> columnToSelect() {
        // we *always* need to select
        Objects.requireNonNull(columnToSelect);
        return columnToSelect;
    }

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
        return new CacheKey<>(
                columnToSelect.valueClass(),
                query,
                columnToSelect.name(),
                columnToSelect.valueClass().getName(),
                columnToSelect.sqlType(),
                columnWhere.name(),
                columnWhere.valueClass().getName(),
                columnWhere.sqlType(),
                columnWhereValue);
    }

    public W columnWhereValue() {
        return this.columnWhereValue;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingleColumnQuery<?, ?> that = (SingleColumnQuery<?, ?>) o;
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
