package co.ecso.jdao.database;

/**
 * DatabaseTable.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 03.09.16
 */
public interface DatabaseTable<T> extends Inserter<T>, Truncater, SingleColumnFinder, MultipleColumnFinder {
}
