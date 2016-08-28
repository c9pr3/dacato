package co.ecso.jdao;

/**
 * ConfigFinder.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 28.08.16
 */
@FunctionalInterface
interface ConfigFinder {
    ApplicationConfig config();
}
