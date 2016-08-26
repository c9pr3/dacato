package co.ecso.daobase.helpers;

/**
 * CreateTableOnly Filter.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 25.04.16
 */
public final class CreateTableOnlyFilter {

    private CreateTableOnlyFilter() {
        //not needed
    }

    public static boolean filter(final String sqlLine) {
        //noinspection RedundantIfStatement
        if (sqlLine.startsWith("--") || sqlLine.startsWith("/") || sqlLine.startsWith("DROP TABLE")
                || sqlLine.startsWith("LOCK TABLES") || sqlLine.startsWith("UNLOCK TABLES")
                || sqlLine.startsWith("INSERT INTO") || sqlLine.startsWith("USE")
                || sqlLine.startsWith("  KEY") || sqlLine.startsWith("  CONSTRAINT")) {
            return false;
        }

        return true;
    }
}
