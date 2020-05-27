package co.ecso.dacato.sqlite;

/**
 * MysqlToSQLiteMapFilter.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @version $Id:$
 * @since 25.04.16
 */
final class MysqlToSQLiteMapFilter {

    private MysqlToSQLiteMapFilter() {
        //not needed
    }

    static String filter(final String s) {
        final String f = s
                .replaceAll("/\\*.*?\\*/", "")
                .replaceAll("`|´", "")
                .replaceAll("(?i)\\) ENGINE.*?;", ");")
                .replaceAll("(?i)(TINY)?INT.*?\\([0-9]+\\)", "INTEGER")
                .replaceAll("(?i)DATETIME", "TIMESTAMP")
                .replaceAll("(?i)NOT NULL", "")
                .replaceAll("(?i)((VAR)?CHAR)([\\s ]+)?(\\([0-9]+\\))?", "TEXT")
                .replaceAll("(?i)(LONGTEXT)([\\s ]+)?(\\([0-9]+\\))?", "TEXT")
                .replaceAll("(?i)UNSIGNED", "")
                .replaceAll("PRIMARY KEY \\(.*?\\)\\)", ")")
                .replaceAll("\\),\\)", "))")
                .replaceAll(",[\\s ]*\\)", ")")
                .toUpperCase();
        return f
                .replaceAll("BIGINTEGER.*?AUTO_INCREMENT", "INTEGER PRIMARY KEY");

    }

}
