package com.manish.shop;

import java.math.BigDecimal;

/** A product for sale. Money is always BigDecimal, never double. */
public record Product(String sku, String name, BigDecimal price) {

    public Product {
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("SKU is required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
        if (price == null || price.signum() < 0) throw new IllegalArgumentException("Price must be zero or positive");
    }

    public static Product of(String sku, String name, String price) {
        return new Product(sku, name, new BigDecimal(price));
    }
}
