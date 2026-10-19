package com.manish.qa.load;

import com.manish.qa.support.TestData;
import com.manish.shop.Cart;
import com.manish.shop.InventoryRepository;
import com.manish.shop.order.Order;
import com.manish.shop.order.OrderService;
import com.manish.shop.order.OrderStatus;
import com.manish.shop.payment.FakePaymentGateway;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static com.manish.qa.support.TestData.BACKPACK;
import static org.testng.Assert.assertEquals;

/**
 * LOAD TESTING
 * "Does it still work correctly at the EXPECTED peak traffic?"
 * 25 concurrent users place 500 orders using TestNG's threadPoolSize + invocationCount.
 * Then we verify nothing was lost or double-counted: stock and charges must add up exactly.
 * (In industry this is usually JMeter, Gatling, k6 or LoadRunner against a deployed server.)
 */
@Test(groups = "load")
public class CheckoutLoadTest {

    private static final int CONCURRENT_USERS = 25;
    private static final int TOTAL_ORDERS = 500;
    private static final int STARTING_STOCK = 10_000;

    private InventoryRepository inventory;
    private FakePaymentGateway gateway;
    private OrderService service;
    private final AtomicInteger confirmed = new AtomicInteger();

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        inventory = TestData.stockedInventory(STARTING_STOCK);
        gateway = new FakePaymentGateway();
        service = TestData.orderService(inventory, gateway);
    }

    @Test(invocationCount = TOTAL_ORDERS, threadPoolSize = CONCURRENT_USERS, invocationTimeOut = 30_000)
    public void concurrentCustomersCheckOut() {
        Cart cart = new Cart();
        cart.addItem(BACKPACK, 1);
        Order order = service.checkout(cart, null, false, FakePaymentGateway.VALID_TOKEN);
        assertEquals(order.status(), OrderStatus.CONFIRMED);
        confirmed.incrementAndGet();
    }

    @Test(dependsOnMethods = "concurrentCustomersCheckOut")
    public void everyOrderWasRecordedExactlyOnce() {
        assertEquals(confirmed.get(), TOTAL_ORDERS);
        assertEquals(inventory.getStock(BACKPACK.sku()), STARTING_STOCK - TOTAL_ORDERS,
                "Stock lost or double-counted under load");
        assertEquals(gateway.getApprovedCharges().size(), TOTAL_ORDERS, "Every order charged exactly once");
    }
}
