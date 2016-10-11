package co.ecso.dacato;

import co.ecso.dacato.database.cache.Cache;
import co.ecso.dacato.database.cache.CacheKey;

import java.util.concurrent.CompletableFuture;

/**
 * AbstractTest.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 17.03.16
 */
public abstract class AbstractTest {

    static {
        System.setProperty("config.file", "src/test/config/application.conf");
    }

    public static final Cache<CacheKey, CompletableFuture> CACHE = new TestApplicationCache<>();

}
