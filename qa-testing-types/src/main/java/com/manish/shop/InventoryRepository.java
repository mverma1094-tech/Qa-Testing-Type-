package com.manish.shop;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory stock store. Methods are synchronized so concurrent buyers can never oversell. */
public class InventoryRepository {

    private final Map<String, Product> products = new ConcurrentHashMap<>();
    private final Map<String, Integer> stock = new HashMap<>();

    public synchronized void addProduct(Product product, int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Quantity must be zero or positive");
        products.put(product.sku(), product);
        stock.merge(product.sku(), quantity, Integer::sum);
    }

    public Optional<Product> findBySku(String sku) {
        return Optional.ofNullable(products.get(sku));
    }

    public synchronized int getStock(String sku) {
        return stock.getOrDefault(sku, 0);
    }

    /** Takes stock out if enough is available. Returns false (and changes nothing) if not. */
    public synchronized boolean reserve(String sku, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        int available = getStock(sku);
        if (available < quantity) return false;
        stock.put(sku, available - quantity);
        return true;
    }

    /** Puts reserved stock back, e.g. after a declined payment. */
    public synchronized void release(String sku, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        stock.merge(sku, quantity, Integer::sum);
    }
}
