package com.manish.shop.payment;

public record PaymentResult(boolean approved, String transactionId, String message) {

    public static PaymentResult approved(String transactionId) {
        return new PaymentResult(true, transactionId, "Approved");
    }

    public static PaymentResult declined(String reason) {
        return new PaymentResult(false, null, reason);
    }
}
