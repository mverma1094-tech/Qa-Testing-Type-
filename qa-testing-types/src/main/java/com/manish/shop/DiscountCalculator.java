package com.manish.shop;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/**
 * Discount rules (the "spec" black-box tests are written from):
 *  - SAVE10: 10% off when subtotal is $50.00 or more
 *  - FLAT5:  $5.00 off
 *  - Members get an extra 5% off
 *  - Total discount can never exceed 30% of the subtotal
 *  - Coupon codes are case-insensitive; unknown codes are rejected
 */
public class DiscountCalculator {

    static final BigDecimal SAVE10_MINIMUM = new BigDecimal("50.00");
    static final BigDecimal SAVE10_RATE = new BigDecimal("0.10");
    static final BigDecimal FLAT5_AMOUNT = new BigDecimal("5.00");
    static final BigDecimal MEMBER_RATE = new BigDecimal("0.05");
    static final BigDecimal MAX_DISCOUNT_RATE = new BigDecimal("0.30");

    public BigDecimal calculateDiscount(BigDecimal subtotal, String couponCode, boolean isMember) {
        if (subtotal == null || subtotal.signum() < 0) {                       // branch B1
            throw new IllegalArgumentException("Subtotal must be zero or positive");
        }

        BigDecimal discount = BigDecimal.ZERO;
        String code = couponCode == null ? "" : couponCode.trim().toUpperCase(Locale.ROOT);

        switch (code) {
            case "" -> { }                                                       // branch B2: no coupon
            case "SAVE10" -> {
                if (subtotal.compareTo(SAVE10_MINIMUM) >= 0) {                   // branch B3 (true/false)
                    discount = discount.add(subtotal.multiply(SAVE10_RATE));
                }
            }
            case "FLAT5" -> discount = discount.add(FLAT5_AMOUNT.min(subtotal)); // branch B4
            // Generic message on purpose: never echo user input back (security)
            default -> throw new IllegalArgumentException("Invalid coupon code"); // branch B5
        }

        if (isMember) {                                                          // branch B6
            discount = discount.add(subtotal.multiply(MEMBER_RATE));
        }

        BigDecimal cap = subtotal.multiply(MAX_DISCOUNT_RATE);
        if (discount.compareTo(cap) > 0) {                                       // branch B7
            discount = cap;
        }
        return discount.setScale(2, RoundingMode.HALF_UP);
    }
}
