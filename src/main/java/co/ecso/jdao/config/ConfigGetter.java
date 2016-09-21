package co.ecso.jdao.config;

/**
 * ConfigGetter.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 28.08.16
 */
@FunctionalInterface
public interface ConfigGetter {
    /**
     * Get application config.
     *
     * @return Application config.
     */
    ApplicationConfig config();
}
