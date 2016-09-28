package co.ecso.jdao.helpers;

import co.ecso.jdao.config.ApplicationConfig;
import co.ecso.jdao.database.CachedDatabaseEntity;
import co.ecso.jdao.database.ColumnList;
import co.ecso.jdao.database.cache.Cache;
import co.ecso.jdao.database.cache.CacheKey;
import co.ecso.jdao.database.query.DatabaseField;
import co.ecso.jdao.database.query.SingleColumnUpdateQuery;

import java.sql.Types;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static co.ecso.jdao.AbstractTest.CACHE;

/**
 * Product.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 24.09.16
 */
public final class Product implements CachedDatabaseEntity<Integer> {
    static final String TABLE_NAME = "products";
    public static final String QUERY = String.format("SELECT %%s FROM %s WHERE products_id = ?", TABLE_NAME);
    private final Integer id;
    private final ApplicationConfig config;
    private AtomicBoolean objectValid = new AtomicBoolean(true);

    Product(final ApplicationConfig config, final Integer id) {
        this.config = config;
        Objects.requireNonNull(id);
        this.id = id;
    }

    @Override
    public Integer primaryKey() {
        return this.id;
    }

    @Override
    public CompletableFuture<Product> save(final ColumnList columnList) {
        SingleColumnUpdateQuery<Integer> query = new SingleColumnUpdateQuery<>(
                String.format("UPDATE %s SET %%s WHERE %%%%s = ?", TABLE_NAME), Fields.ID, id, columnList);
        final CompletableFuture<Integer> updated = this.update(query, () -> this.objectValid);
        this.objectValid.set(false);
        return updated.thenApply(l -> new Product(config, id));
    }

    @Override
    public ApplicationConfig config() {
        return config;
    }

    @Override
    public Cache<CacheKey, CompletableFuture> cache() {
        return CACHE;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                '}';
    }

    static final class Fields {
        static final DatabaseField<Integer> ID = new DatabaseField<>("products_id", Integer.class, Types.INTEGER);
        static final DatabaseField<String> EAN = new DatabaseField<>("products_ean", String.class, Types.VARCHAR);
        static final DatabaseField<Integer> QUANTITY =
                new DatabaseField<>("products_quantity", Integer.class, Types.INTEGER);
        static final DatabaseField<Integer> SHIPPING_TIME =
                new DatabaseField<>("products_shippingtime", Integer.class, Types.INTEGER);
        static final DatabaseField<String> MODEL = new DatabaseField<>("products_model", String.class, Types.VARCHAR);
        static final DatabaseField<Integer> GROUP_PERMISSION_0 =
                new DatabaseField<>("group_permission_0", Integer.class, Types.TINYINT);
        static final DatabaseField<Integer> GROUP_PERMISSION_1 =
                new DatabaseField<>("group_permission_1", Integer.class, Types.TINYINT);
        static final DatabaseField<Integer> GROUP_PERMISSION_2 =
                new DatabaseField<>("group_permission_2", Integer.class, Types.TINYINT);
        static final DatabaseField<Integer> GROUP_PERMISSION_3 =
                new DatabaseField<>("group_permission_3", Integer.class, Types.TINYINT);
        static final DatabaseField<Integer> GROUP_PERMISSION_4 =
                new DatabaseField<>("group_permission_4", Integer.class, Types.TINYINT);
        static final DatabaseField<Integer> SORT = new DatabaseField<>("products_sort", Integer.class, Types.INTEGER);
        static final DatabaseField<String> IMAGE = new DatabaseField<>("products_image", String.class, Types.VARCHAR);
        static final DatabaseField<Float> PRICE =
                new DatabaseField<>("products_price", Float.class, Types.DECIMAL);
        static final DatabaseField<Float> DISCOUNT_ALLOWED =
                new DatabaseField<>("products_discount_allowed", Float.class, Types.DECIMAL);
        static final DatabaseField<Date> DATE_ADDED =
                new DatabaseField<>("products_date_added", Date.class, Types.DATE);
        static final DatabaseField<Date> LAST_MODIFIED =
                new DatabaseField<>("products_last_modified", Date.class, Types.DATE);
        static final DatabaseField<Date> AVAILABLE =
                new DatabaseField<>("products_date_available", Date.class, Types.DATE);
        static final DatabaseField<Float> WEIGHT =
                new DatabaseField<>("products_weight", Float.class, Types.DECIMAL);
        static final DatabaseField<Integer> STATUS =
                new DatabaseField<>("products_status", Integer.class, Types.TINYINT);
        static final DatabaseField<Integer> TAX_CLASS_ID =
                new DatabaseField<>("products_tax_class_id", Integer.class, Types.INTEGER);
        static final DatabaseField<String> TEMPLATE =
                new DatabaseField<>("product_template", String.class, Types.VARCHAR);
        static final DatabaseField<String> OPTIONS_TEMPLATE =
                new DatabaseField<>("options_template", String.class, Types.VARCHAR);
        static final DatabaseField<Integer> MANUFACTURERS_ID =
                new DatabaseField<>("manufacturers_id", Integer.class, Types.INTEGER);
        static final DatabaseField<String> MANUFACTURERS_MODEL =
                new DatabaseField<>("products_manufacturers_model", String.class, Types.VARCHAR);
        static final DatabaseField<Integer> ORDERED =
                new DatabaseField<>("products_ordered", Integer.class, Types.INTEGER);
        static final DatabaseField<Integer> FSK_18 =
                new DatabaseField<>("products_fsk18", Integer.class, Types.INTEGER);
        static final DatabaseField<Integer> VPE = new DatabaseField<>("products_vpe", Integer.class, Types.INTEGER);
        static final DatabaseField<Integer> VPE_STATUS =
                new DatabaseField<>("products_vpe_status", Integer.class, Types.INTEGER);
        static final DatabaseField<Float> VPE_VALUE =
                new DatabaseField<>("products_vpe_value", Float.class, Types.DECIMAL);
        static final DatabaseField<Integer> STARTPAGE =
                new DatabaseField<>("products_startpage", Integer.class, Types.INTEGER);
        static final DatabaseField<Integer> STARTPAGE_SORT =
                new DatabaseField<>("products_startpage_sort", Integer.class, Types.INTEGER);
    }
}
