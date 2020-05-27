package co.ecso.dacato;

import co.ecso.dacato.database.cache.Cache;

/**
 * AbstractTest.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @since 17.03.16
 */
public abstract class AbstractTest {

    public static final Cache CACHE = new TestApplicationCache();

    static {
        System.setProperty("config.file", "src/test/config/application.conf");
    }

}
