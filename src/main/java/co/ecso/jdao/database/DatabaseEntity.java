package co.ecso.jdao.database;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

/**
 * DatabaseEntity.
 *
 * @param <T> Type of the auto_inc field of this entity, usually Long.
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 08.08.16
 */
@SuppressWarnings("unused")
public interface DatabaseEntity<T> extends EntityFinder, Updater<T> {
    T id();

    CompletableFuture<Boolean> save(final SingleColumnUpdateQuery<T> query);

    String toJson() throws SQLException;

    void checkValidity();

}
