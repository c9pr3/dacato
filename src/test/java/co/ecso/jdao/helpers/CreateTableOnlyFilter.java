package co.ecso.jdao.helpers;

/**
 * CreateTableOnly Filter.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 25.04.16
 */
public final class CreateTableOnlyFilter {

    private CreateTableOnlyFilter() {
        //unused
    }

    public static boolean filter(final String sqlLine) {
        return !sqlLine.matches("^(--|/|DROP TABLE|LOCK TABLES|UNLOCK TABLES|INSERT INTO|USE|  KEY|  CONSTRAINT).*$");
    }
}
