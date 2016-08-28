package co.ecso.jdao;

/**
 * ConfigGetter.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 28.08.16
 */
@FunctionalInterface
interface ConfigGetter {
    ApplicationConfig config();
}
