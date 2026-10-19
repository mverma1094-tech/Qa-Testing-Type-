package com.manish.qa.integration;

import com.manish.qa.support.TestData;
import com.manish.shop.Cart;
import com.manish.shop.InventoryRepository;
import com.manish.shop.order.Order;
import com.manish.shop.order.OrderService;
import com.manish.shop.order.OrderStatus;
import com.manish.shop.order.OutOfStockException;
import com.manish.shop.payment.FakePaymentGateway;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static com.manish.qa.support.TestData.*;
import static org.testng.Assert.*;

/**
 * INTEGRATION TESTING
 * Real Cart + DiscountCalculator + InventoryRepository + OrderService working together.
 * Unit tests prove each piece works alone; these prove the pieces work TOGETHER
 * (correct data passed between them, rollbacks across components, etc.).
 * Only the external payment provider is replaced with a fake.
 */
@Test(groups = "integration")
public class OrderServiceIntegrationTest {

    private InventoryRepository inventory;
    private FakePaymentGateway gateway;
    private OrderService orderService;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        inventory = TestData.stockedInventory(5);
        gateway = new FakePaymentGateway();
        orderService = TestData.orderService(inventory, gateway);
    }

    public void successfulCheckoutCalculatesTotalsAndReducesStock() {
        Cart cart = new Cart();
        cart.addItem(BACKPACK, 2);   // 59.98
        cart.addItem(BIKE_LIGHT, 1); //  9.99  -> subtotal 69.97

        Order order = orderService.checkout(cart, "SAVE10", false, FakePaymentGateway.VALID_TOKEN);

        assertEquals(order.status(), OrderStatus.CONFIRMED);
        assertEquals(order.subtotal(), new BigDecimal("69.97"));
        assertEquals(order.discount(), new BigDecimal("7.00"));  // 10% of 69.97 = 6.997
        assertEquals(order.tax(), new BigDecimal("3.78"));       // 6% of 62.97 = 3.7782
        assertEquals(order.total(), new BigDecimal("66.75"));
        assertEquals(inventory.getStock(BACKPACK.sku()), 3);
        assertEquals(inventory.getStock(BIKE_LIGHT.sku()), 4);
        assertEquals(gateway.getApprovedCharges().get(0), new BigDecimal("66.75"),
                "Gateway must be charged the exact order total");
    }

    public void declinedPaymentReleasesReservedStock() {
        Cart cart = new Cart();
        cart.addItem(FLEECE, 2);

        Order order = orderService.checkout(cart, null, false, FakePaymentGateway.DECLINED_TOKEN);

        assertEquals(order.status(), OrderStatus.PAYMENT_DECLINED);
        assertEquals(inventory.getStock(FLEECE.sku()), 5, "Stock must be returned after a declined payment");
        assertTrue(gateway.getApprovedCharges().isEmpty());
    }

    public void outOfStockOnSecondItemRollsBackTheFirst() {
        inventory.reserve(ONESIE.sku(), 5); // someone else bought every onesie
        Cart cart = new Cart();
        cart.addItem(BACKPACK, 1);
        cart.addItem(ONESIE, 1);

        assertThrows(OutOfStockException.class,
                () -> orderService.checkout(cart, null, false, FakePaymentGateway.VALID_TOKEN));
        assertEquals(inventory.getStock(BACKPACK.sku()), 5, "Backpack reservation must be rolled back");
        assertTrue(gateway.getApprovedCharges().isEmpty(), "Customer must not be charged");
    }

    public void invalidCouponIsRejectedBeforeStockIsTouched() {
        Cart cart = new Cart();
        cart.addItem(BACKPACK, 1);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.checkout(cart, "FAKECODE", false, FakePaymentGateway.VALID_TOKEN));
        assertEquals(inventory.getStock(BACKPACK.sku()), 5);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void emptyCartCannotCheckOut() {
        orderService.checkout(new Cart(), null, false, FakePaymentGateway.VALID_TOKEN);
    }
}
