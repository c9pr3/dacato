package co.ecso.dacato.cassandra;

/**
 * MysqlToPsqlMapFilter.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 25.04.16
 */
final class MysqlToCassandraMapFilter {

    private MysqlToCassandraMapFilter() {
        //not needed
    }

    static String filter(final String s) {
        return s
                .replaceAll("/\\*.*?\\*/", "")
                .replaceAll("`|´", "")
                .replaceAll("(?i)(NOT|DEFAULT) (NULL|'.*?')", "")
                .replaceAll("(?i)AUTO_INCREMENT", "")
                .replaceAll("\\([0-9]+(,[0-9]+)?\\)", "")
                .replaceAll("(?i)TINYINT", "INT")
                .replaceAll("(?i)DATETIME", "TIMESTAMP")
                .replaceAll("(?i)\\) ENGINE.*?;", "); ")
                .replaceAll(",\\)", ")")
                .replaceAll("\\),\\)", "))");
    }

}
