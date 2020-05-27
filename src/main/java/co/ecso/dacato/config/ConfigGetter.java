package co.ecso.dacato.config;

/**
 * ConfigGetter.
 *
 * @author Christian Scharmach (cs@e-cs.co)
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
