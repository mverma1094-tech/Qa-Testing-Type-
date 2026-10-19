package com.manish.shop.order;

public class OutOfStockException extends RuntimeException {

    private final String sku;

    public OutOfStockException(String sku) {
        super("Not enough stock for " + sku);
        this.sku = sku;
    }

    public String getSku() {
        return sku;
    }
}
