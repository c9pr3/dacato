package co.ecso.jdao.database;

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
public final class SingleColumnQuery<S, W> {

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

    public String query() {
        return query;
    }

    public W columnWhereValue() {
        return this.columnWhereValue;
    }
}
