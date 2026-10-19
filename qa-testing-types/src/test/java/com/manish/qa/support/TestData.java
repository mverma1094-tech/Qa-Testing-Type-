package com.manish.qa.support;

import com.manish.shop.DiscountCalculator;
import com.manish.shop.InventoryRepository;
import com.manish.shop.Product;
import com.manish.shop.order.OrderService;
import com.manish.shop.payment.FakePaymentGateway;

/** Shared test fixtures so every test uses the same products and prices. */
public final class TestData {

    public static final Product BACKPACK   = Product.of("SL-004", "Sauce Labs Backpack", "29.99");
    public static final Product BIKE_LIGHT = Product.of("SL-000", "Sauce Labs Bike Light", "9.99");
    public static final Product FLEECE     = Product.of("SL-005", "Sauce Labs Fleece Jacket", "49.99");
    public static final Product ONESIE     = Product.of("SL-002", "Sauce Labs Onesie", "7.99");

    private TestData() { }

    /** A repository stocked with every test product. */
    public static InventoryRepository stockedInventory(int unitsEach) {
        InventoryRepository repo = new InventoryRepository();
        repo.addProduct(BACKPACK, unitsEach);
        repo.addProduct(BIKE_LIGHT, unitsEach);
        repo.addProduct(FLEECE, unitsEach);
        repo.addProduct(ONESIE, unitsEach);
        return repo;
    }

    public static OrderService orderService(InventoryRepository repo, FakePaymentGateway gateway) {
        return new OrderService(repo, new DiscountCalculator(), gateway);
    }
}
