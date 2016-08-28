package co.ecso.jdao;

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
@SuppressWarnings("WeakerAccess")
public interface DatabaseEntity {
    Long id();

    CompletableFuture<? extends DatabaseEntity> save(final Map<DatabaseField<?>, ? extends Comparable> map);

    String toJson() throws SQLException;

    void checkValidity();
}
