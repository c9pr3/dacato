package co.ecso.jdao.database;

/**
 * CachedDatabaseTable.
 *
 * @param <T> Type of the auto_inc field of this table, usually Long.
 * @param <E> The Entity-Class which is being used.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 17.09.16
 */
public interface CachedDatabaseTable<T, E extends DatabaseEntity<T>> extends DatabaseTable<T, E> {
//
//    @Override
//    public CompletableFuture<Boolean> truncate(String query) {
//        return null;
//    }
//
//    @Override
//    public CompletableFuture<DatabaseResultField<T>> add(InsertQuery<T> query) {
//        return null;
//    }
//
//    @Override
//    public <S, W> CompletableFuture<List<DatabaseResultField<S>>> findMany(SingleColumnQuery<S, W> query) {
//        return null;
//    }
//
//    @Override
//    public DatabaseResultField<T> getResult(String finalQuery, DatabaseField<T> columnToSelect,
// PreparedStatement stmt) throws SQLException {
//        return null;
//    }
//
//    @Override
//    public <S> CompletableFuture<DatabaseResultField<S>> findOne(MultiColumnQuery<S> query) {
//        return null;
//    }
//
//    @Override
//    public <S, W> CompletableFuture<DatabaseResultField<S>> findOne(SingleColumnQuery<S, W> query) {
//        return null;
//    }
//
//    @Override
//    public <R> List<DatabaseResultField<R>> getListRowResult(String finalQuery, DatabaseField<R> columnToSelect,
// PreparedStatement stmt) throws SQLException {
//        return null;
//    }
//
//    @Override
//    public <R> DatabaseResultField<R> getSingleRowResult(String finalQuery, DatabaseField<R> columnToSelect,
// PreparedStatement stmt) throws SQLException {
//        return null;
//    }
//
//    @Override
//    public CompletableFuture<E> findOne(T id) {
//        return null;
//    }
//
//    @Override
//    public CompletableFuture<List<E>> findAll() {
//        return null;
//    }
}
