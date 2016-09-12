package co.ecso.jdao.database;

import co.ecso.jdao.config.ConfigGetter;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * DatabaseTable.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 03.09.16
 */
public interface DatabaseTable<T, R extends DatabaseEntity<T>> extends EntityFinder<T>, Truncater, Inserter<T, R>,
        StatementFiller, ConfigGetter {

    CompletableFuture<R> findOne(final T id);

    CompletableFuture<List<R>> findAll();
}
