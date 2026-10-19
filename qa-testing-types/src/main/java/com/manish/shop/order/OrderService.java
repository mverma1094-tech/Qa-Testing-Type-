package com.manish.shop.order;

import com.manish.shop.Cart;
import com.manish.shop.DiscountCalculator;
import com.manish.shop.InventoryRepository;
import com.manish.shop.Product;
import com.manish.shop.payment.PaymentGateway;
import com.manish.shop.payment.PaymentResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Checkout flow that ties the components together:
 * validate discount -> reserve stock -> calculate tax -> charge payment -> confirm (or roll back).
 */
public class OrderService {

    public static final BigDecimal TAX_RATE = new BigDecimal("0.06");

    private final InventoryRepository inventory;
    private final DiscountCalculator discountCalculator;
    private final PaymentGateway paymentGateway;

    public OrderService(InventoryRepository inventory, DiscountCalculator discountCalculator, PaymentGateway paymentGateway) {
        this.inventory = inventory;
        this.discountCalculator = discountCalculator;
        this.paymentGateway = paymentGateway;
    }

    public Order checkout(Cart cart, String couponCode, boolean isMember, String paymentToken) {
        if (cart == null || cart.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        BigDecimal subtotal = cart.getSubtotal();
        // Validate the coupon before touching stock, so a bad coupon never reserves anything
        BigDecimal discount = discountCalculator.calculateDiscount(subtotal, couponCode, isMember);

        Map<Product, Integer> reserved = new LinkedHashMap<>();
        for (Map.Entry<Product, Integer> line : cart.getItems().entrySet()) {
            Product product = line.getKey();
            int qty = line.getValue();
            if (!inventory.reserve(product.sku(), qty)) {
                releaseAll(reserved);                       // roll back earlier lines
                throw new OutOfStockException(product.sku());
            }
            reserved.put(product, qty);
        }

        BigDecimal taxable = subtotal.subtract(discount);
        BigDecimal tax = taxable.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = taxable.add(tax);

        PaymentResult payment = paymentGateway.charge(paymentToken, total);
        String orderId = "ORD-" + UUID.randomUUID();

        if (!payment.approved()) {
            releaseAll(reserved);
            return new Order(orderId, subtotal, discount, tax, total, OrderStatus.PAYMENT_DECLINED);
        }
        return new Order(orderId, subtotal, discount, tax, total, OrderStatus.CONFIRMED);
    }

    private void releaseAll(Map<Product, Integer> reserved) {
        reserved.forEach((product, qty) -> inventory.release(product.sku(), qty));
    }
}
