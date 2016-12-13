package co.ecso.dacato.database.transaction;

import co.ecso.dacato.config.ConfigGetter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Transaction.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @since 02.12.16
 */
public interface Transaction extends ConfigGetter {
//    private final Queue<CompletableFuture<?>> futures = new ConcurrentLinkedQueue<>();
//    private volatile boolean started;

    Connection connection() throws SQLException;

    default void add(final CompletableFuture<?> a) {
        this.futures().add(a);
    }

    ConcurrentLinkedQueue<CompletableFuture<?>> futures();

    default boolean start() throws SQLException {
        if (this.connection() == null) {
            throw new SQLException("Could not obtain connection");
        }
        if (!this.connection().getAutoCommit()) {
            return false;
        }
//        this.started = true;
        this.connection().setAutoCommit(false);
        return true;
    }

    default CompletableFuture<List<?>> commit() throws SQLException, InterruptedException, ExecutionException,
            TimeoutException {
        final CompletableFuture<List<?>> retValFuture = new CompletableFuture<>();
        final List<Object> completedFutures = new LinkedList<>();

        // @TODO find a better solution
        // this one exists, because of exceptions who would disappear
        // if done by CompletableFuture.allOf(this.futures....)
        for (final CompletableFuture<?> future : this.futures()) {
            completedFutures.add(future.get(10, TimeUnit.SECONDS));
        }

        try {
            this.futures().clear();
            connection().commit();
            connection().setAutoCommit(true);
            connection().close();
            retValFuture.complete(completedFutures);
//            this.started = false;
        } catch (final SQLException e) {
            retValFuture.completeExceptionally(e);
            e.printStackTrace();
        }

        return retValFuture;
    }

    default CompletableFuture<List<?>> rollback() throws SQLException, InterruptedException, ExecutionException,
            TimeoutException {
        final CompletableFuture<List<?>> retValFuture = new CompletableFuture<>();
        final List<Object> completedFutures = new LinkedList<>();

        // @TODO find a better solution
        // this one exists, because of exceptions who would disappear
        // if done by CompletableFuture.allOf(this.futures....)
        for (final CompletableFuture<?> future : this.futures()) {
            completedFutures.add(future.get(10, TimeUnit.SECONDS));
        }

        CompletableFuture.runAsync(() -> {
            try {
                this.futures().clear();
                connection().rollback();
                connection().setAutoCommit(true);
                connection().close();
                retValFuture.complete(completedFutures);
//                this.started = false;
            } catch (final SQLException e) {
                retValFuture.completeExceptionally(e);
                e.printStackTrace();
            }
        });

        return retValFuture;
    }

}
