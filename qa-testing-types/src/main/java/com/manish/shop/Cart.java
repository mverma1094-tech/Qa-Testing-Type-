package com.manish.shop;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** Shopping cart. Business rule: 1 to 10 units of any single product. */
public class Cart {

    public static final int MIN_QTY = 1;
    public static final int MAX_QTY_PER_ITEM = 10;

    private final Map<Product, Integer> items = new LinkedHashMap<>();

    public void addItem(Product product, int quantity) {
        Objects.requireNonNull(product, "product");
        if (quantity < MIN_QTY || quantity > MAX_QTY_PER_ITEM) {
            throw new IllegalArgumentException("Quantity must be between " + MIN_QTY + " and " + MAX_QTY_PER_ITEM);
        }
        int newQty = items.getOrDefault(product, 0) + quantity;
        if (newQty > MAX_QTY_PER_ITEM) {
            throw new IllegalArgumentException("Cannot have more than " + MAX_QTY_PER_ITEM + " of one product");
        }
        items.put(product, newQty);
    }

    public void removeItem(Product product) {
        items.remove(product);
    }

    public int getQuantity(Product product) {
        return items.getOrDefault(product, 0);
    }

    public int getItemCount() {
        return items.values().stream().mapToInt(Integer::intValue).sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public Map<Product, Integer> getItems() {
        return Collections.unmodifiableMap(items);
    }

    public BigDecimal getSubtotal() {
        return items.entrySet().stream()
                .map(e -> e.getKey().price().multiply(BigDecimal.valueOf(e.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
