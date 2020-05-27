package co.ecso.dacato.postgresql;

/**
 * MysqlToPsqlMapFilter.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @version $Id:$
 * @since 25.04.16
 */
final class MysqlToPsqlMapFilter {

    private MysqlToPsqlMapFilter() {
        //not needed
    }

    static String filter(final String s) {
        return s
                .replaceAll("/\\*.*?\\*/", "")
                .replaceAll("`|´", "")
                .replaceAll("(?i)\\) ENGINE.*?;", ");")
                .replaceAll("(?i)BIGINT.*?\\([0-9]+\\).*?AUTO_INCREMENT", "BIGSERIAL")
                .replaceAll("(?i)(TINY)?INT.*?\\([0-9]+\\).*?AUTO_INCREMENT", "SERIAL")
                .replaceAll("(?i)BIGINT.*?\\([0-9]+\\)", "BIGINT")
                .replaceAll("(?i)(TINY)?INT.*?\\([0-9]+\\)", "INTEGER")
                .replaceAll("(?i)AUTO_INCREMENT", "")
                .replaceAll("(?i)DATETIME", "TIMESTAMP")
                .replaceAll("(?i)NOT NULL", "")
                .replaceAll("(?i)((VAR)?CHAR)([\\s ]+)?(\\([0-9]+\\))?", "TEXT")
                .replaceAll("(?i)(LONGTEXT)([\\s ]+)?(\\([0-9]+\\))?", "TEXT")
                .replaceAll("(?i)UNSIGNED", "")
                .replaceAll("\\),\\)", "))");
    }

}
