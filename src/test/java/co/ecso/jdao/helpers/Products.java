package co.ecso.jdao.helpers;

import co.ecso.jdao.AbstractTest;
import co.ecso.jdao.config.ApplicationConfig;
import co.ecso.jdao.database.CachedDatabaseTable;
import co.ecso.jdao.database.cache.Cache;
import co.ecso.jdao.database.cache.CacheKey;
import co.ecso.jdao.database.query.InsertQuery;
import co.ecso.jdao.database.query.SingleColumnQuery;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Products.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 24.09.16
 */
public final class Products implements CachedDatabaseTable<Integer, Product> {

    private static final Cache<CacheKey, CompletableFuture> CACHE = AbstractTest.CACHE;
    private final ApplicationConfig config;

    public Products(final ApplicationConfig config) {
        this.config = config;
    }

    @Override
    public CompletableFuture<Product> findOne(final Integer id) {
        return findOne(new SingleColumnQuery<>(
                "SELECT %s FROM " + Product.TABLE_NAME + " WHERE %s = ?",
                Product.Fields.ID, Product.Fields.ID, id
        )).thenApply(id1 -> new Product(config, id1.resultValue()));
    }

    public CompletableFuture<Product> add(final String ean, final Integer quantity, final Integer shippingTime,
                                          final String model, final Integer groupPermission0,
                                          final Integer groupPermission1, final Integer groupPermission2,
                                          final Integer groupPermission3, final Integer groupPermission4,
                                          final Integer sort, final String image, final BigDecimal price,
                                          final BigDecimal discountAllowed, final Date dateAdded,
                                          final Date lastModified, final Date available, final BigDecimal weight,
                                          final Integer status, final Integer taxClassId, final String template,
                                          final String optionsTemplate, final Integer manufacturersId,
                                          final String manufacturersModel, final Integer odererd, final Integer fsk18,
                                          final Integer vpe, final Integer vpeStatus, final BigDecimal vpeValue,
                                          final Integer startPage, final Integer startPageSort) {

        final InsertQuery<Integer> query = new InsertQuery<>(String.format("INSERT INTO %s VALUES (null, ?, ?, ?, " +
                        "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                Product.TABLE_NAME), Product.Fields.ID);

        query.add(Product.Fields.EAN, ean);
        query.add(Product.Fields.QUANTITY, quantity);
        query.add(Product.Fields.SHIPPING_TIME, shippingTime);
        query.add(Product.Fields.MODEL, model);
        query.add(Product.Fields.GROUP_PERMISSION_0, groupPermission0);
        query.add(Product.Fields.GROUP_PERMISSION_1, groupPermission1);
        query.add(Product.Fields.GROUP_PERMISSION_2, groupPermission2);
        query.add(Product.Fields.GROUP_PERMISSION_3, groupPermission3);
        query.add(Product.Fields.GROUP_PERMISSION_4, groupPermission4);
        query.add(Product.Fields.SORT, sort);
        query.add(Product.Fields.IMAGE, image);
        query.add(Product.Fields.PRICE, price);
        query.add(Product.Fields.DISCOUNT_ALLOWED, discountAllowed);
        query.add(Product.Fields.DATE_ADDED, dateAdded);
        query.add(Product.Fields.LAST_MODIFIED, lastModified);
        query.add(Product.Fields.AVAILABLE, available);
        query.add(Product.Fields.WEIGHT, weight);
        query.add(Product.Fields.STATUS, status);
        query.add(Product.Fields.TAX_CLASS_ID, taxClassId);
        query.add(Product.Fields.TEMPLATE, template);
        query.add(Product.Fields.OPTIONS_TEMPLATE, optionsTemplate);
        query.add(Product.Fields.MANUFACTURERS_ID, manufacturersId);
        query.add(Product.Fields.MANUFACTURERS_MODEL, manufacturersModel);
        query.add(Product.Fields.ORDERED, odererd);
        query.add(Product.Fields.FSK_18, fsk18);
        query.add(Product.Fields.VPE, vpe);
        query.add(Product.Fields.VPE_STATUS, vpeStatus);
        query.add(Product.Fields.VPE_VALUE, vpeValue);
        query.add(Product.Fields.STARTPAGE, startPage);
        query.add(Product.Fields.STARTPAGE_SORT, startPageSort);

        return add(query).thenApply(id -> new Product(config, id.resultValue()));
    }

    @Override
    public CompletableFuture<List<Product>> findAll() {
        return this.findAll(new SingleColumnQuery<>("SELECT %s FROM " + Product.TABLE_NAME, Product.Fields.ID))
                .thenApply(list -> list.stream().map(foundId -> new Product(config, foundId.resultValue()))
                        .collect(Collectors.toList()));
    }

    public CompletableFuture<Boolean> removeAll() {
        return truncate(String.format("TRUNCATE TABLE %s", Product.TABLE_NAME));
    }

    @Override
    public ApplicationConfig config() {
        return config;
    }

    @Override
    public Cache<CacheKey, CompletableFuture> cache() {
        return CACHE;
    }
}
