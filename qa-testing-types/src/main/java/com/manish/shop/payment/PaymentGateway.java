package com.manish.shop.payment;

import java.math.BigDecimal;

/** Boundary to an external payment provider. Tests plug in FakePaymentGateway. */
public interface PaymentGateway {
    PaymentResult charge(String paymentToken, BigDecimal amount);
}
