package co.ecso.dacato.database.cache;

import co.ecso.dacato.database.query.Truncater;
import co.ecso.dacato.database.querywrapper.TruncateQuery;

import java.util.concurrent.CompletableFuture;

/**
 * CachedTruncater.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @version $Id:$
 * @since 19.09.16
 */
public interface CachedTruncater extends Truncater, CacheGetter {

    @Override
    default CompletableFuture<Boolean> truncate(final TruncateQuery<?> query) {
        cache().keySet().stream()
                .filter(k -> k.hasKey(query.tableName()))
                .forEach(k -> cache().invalidate(k));
        return Truncater.super.truncate(query);
    }

}
