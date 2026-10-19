package com.manish.shop.payment;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Stand-in for a real payment provider (like Stripe's test mode).
 *  - token "tok_declined" is always declined
 *  - a blank token or an amount of zero or less is declined
 *  - everything else is approved
 * Thread-safe so load and stress tests can use it.
 */
public class FakePaymentGateway implements PaymentGateway {

    public static final String DECLINED_TOKEN = "tok_declined";
    public static final String VALID_TOKEN = "tok_visa";

    private final List<BigDecimal> approvedCharges = new CopyOnWriteArrayList<>();

    @Override
    public PaymentResult charge(String paymentToken, BigDecimal amount) {
        if (paymentToken == null || paymentToken.isBlank()) return PaymentResult.declined("Missing payment token");
        if (amount == null || amount.signum() <= 0) return PaymentResult.declined("Invalid amount");
        if (DECLINED_TOKEN.equals(paymentToken)) return PaymentResult.declined("Card declined");

        approvedCharges.add(amount);
        return PaymentResult.approved("txn_" + UUID.randomUUID());
    }

    public List<BigDecimal> getApprovedCharges() {
        return Collections.unmodifiableList(approvedCharges);
    }
}
