package co.ecso.dacato.cassandra;

import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.database.DatabaseTable;
import co.ecso.dacato.database.query.*;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Customers.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 15.03.16
 */
final class CassandraCustomers implements DatabaseTable<byte[], CassandraCustomer> {

    private final ApplicationConfig config;

    CassandraCustomers(final ApplicationConfig config) {
        this.config = config;
    }

    @Override
    public CompletableFuture<CassandraCustomer> findOne(final byte[] primaryKey) {
        return this.findOne(new SingleColumnQuery<>("SELECT %s FROM customer WHERE %s = ?",
                CassandraCustomer.Fields.ID, CassandraCustomer.Fields.ID, primaryKey)).thenApply(foundId ->
                new CassandraCustomer(config, (UUID) foundId.resultValuePOJO()));
    }

    @Override
    public CompletableFuture<List<CassandraCustomer>> findAll() {
        return this.findAll(new SingleColumnQuery<>("SELECT %s FROM customer", CassandraCustomer.Fields.ID))
                .thenApply(list -> list.stream().map(foundId ->
                        new CassandraCustomer(config, (UUID) foundId.resultValuePOJO())).collect(Collectors.toList()));
    }

    CompletableFuture<CassandraCustomer> findOneByFirstName(final String firstName) {
        final SingleColumnQuery<byte[], String> query = new SingleColumnQuery<>("SELECT %s FROM customer " +
                "WHERE %s = ? LIMIT 1", CassandraCustomer.Fields.ID, CassandraCustomer.Fields.FIRST_NAME, firstName);
        return this.findOne(query).thenApply(foundId ->
                new CassandraCustomer(config, (UUID) foundId.resultValuePOJO()));
    }

    CompletableFuture<List<CassandraCustomer>> findAllByFirstName(final String firstName) {
        final SingleColumnQuery<byte[], String> query = new SingleColumnQuery<>("SELECT %s FROM customer WHERE %s = ?",
                CassandraCustomer.Fields.ID,
                CassandraCustomer.Fields.FIRST_NAME, firstName);
        return this.findMany(query).thenApply(list ->
                list.stream().map(l -> new CassandraCustomer(config, (UUID) l.resultValuePOJO()))
                        .collect(Collectors.toList()));
    }

    CompletableFuture<Map<DatabaseField, DatabaseResultField>> findFirstNameById(final byte[] id) {
        final String queryStr = "SELECT %s FROM customer WHERE %s = ?";
        final List<DatabaseField> columnsToSelect = new LinkedList<>();
        columnsToSelect.add(CassandraCustomer.Fields.FIRST_NAME);
        final Map<DatabaseField<?>, Object> map = new HashMap<>();
        map.put(CassandraCustomer.Fields.ID, id);
        return findOne(new MultiColumnSelectQuery<>(queryStr, columnsToSelect, () -> map));
    }

    CompletableFuture<List<Map<DatabaseField, DatabaseResultField>>> findManyFirstName() {
        final String queryStr = "SELECT %s FROM customer";
        final List<DatabaseField> columnsToSelect = new LinkedList<>();
        columnsToSelect.add(CassandraCustomer.Fields.FIRST_NAME);
        final Map<DatabaseField<?>, Object> map = new HashMap<>();
        return findMany(new MultiColumnSelectQuery<>(queryStr, columnsToSelect, () -> map));
    }

    CompletableFuture<Boolean> removeAll() {
        return this.truncate("TRUNCATE TABLE customer");
    }

    @Override
    public ApplicationConfig config() {
        return config;
    }

    public CompletableFuture<CassandraCustomer> create(final String firstName, final Long number) {
        final InsertQuery<byte[]> query = new InsertQuery<>(
                "INSERT INTO customer (%s, %s, %s) VALUES (?, ?, ?)");
        final UUID uuid1 = UUID.randomUUID();
        long hi = uuid1.getMostSignificantBits();
        long lo = uuid1.getLeastSignificantBits();
        byte[] uuidb = ByteBuffer.allocate(16).putLong(hi).putLong(lo).array();
        query.add(CassandraCustomer.Fields.ID, uuidb);
        query.add(CassandraCustomer.Fields.FIRST_NAME, firstName);
        query.add(CassandraCustomer.Fields.NUMBER, number);
        return this.add(query).thenApply(newId -> new CassandraCustomer(config, uuidb));
    }

    CompletableFuture<Integer> removeOne(final byte[] id) {
        Map<DatabaseField<?>, Object> map = new HashMap<>();
        map.put(CassandraCustomer.Fields.ID, id);
        return this.removeOne(new RemoveQuery<>("DELETE FROM customer WHERE %s = ?", () -> map));
    }

}
