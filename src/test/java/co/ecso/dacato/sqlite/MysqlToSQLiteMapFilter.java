package co.ecso.dacato.sqlite;

/**
 * MysqlToSQLiteMapFilter.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 25.04.16
 */
final class MysqlToSQLiteMapFilter {

    private MysqlToSQLiteMapFilter() {
        //not needed
    }

    static String filter(final String s) {
        return s
                .replaceAll("/\\*.*?\\*/", "")
                .replaceAll("`|´", "")
                .replaceAll("(?i)\\) ENGINE.*?;", ");")
                .replaceAll("(?i)(TINY)?INT.*?\\([0-9]+\\)", "INTEGER")
                .replaceAll("(?i)AUTO_INCREMENT", "")
                .replaceAll("(?i)DATETIME", "TIMESTAMP")
                .replaceAll("(?i)NOT NULL", "")
                .replaceAll("(?i)((VAR)?CHAR)([\\s ]+)?(\\([0-9]+\\))?", "TEXT")
                .replaceAll("(?i)(LONGTEXT)([\\s ]+)?(\\([0-9]+\\))?", "TEXT")
                .replaceAll("(?i)UNSIGNED", "")
                .replaceAll("\\),\\)", "))")
                .toUpperCase();
    }

}
