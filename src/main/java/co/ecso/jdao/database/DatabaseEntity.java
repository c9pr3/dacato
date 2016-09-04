package co.ecso.jdao.database;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * DatabaseEntity.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 08.08.16
 */
@SuppressWarnings("unused")
public interface DatabaseEntity<T> extends Updater, SingleColumnFinder, MultipleColumnFinder {
    T id();

    CompletableFuture<? extends DatabaseEntity> save(final Map<DatabaseField<?>, ?> map,
                                                     final Map<DatabaseField<?>, ?> whereMap);

    String toJson() throws SQLException;

    void checkValidity();

    default <R> CompletableFuture<R> findById(final String query, final DatabaseField<R> field,
                                                        final DatabaseField<T> idField) {
        return find(new SingleFindQuery<>(query, field,
                ColumnList.get(idField, CompletableFuture.completedFuture(this.id()))
        ));
    }

}
