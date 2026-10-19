package com.manish.qa.stress;

import com.manish.qa.support.TestData;
import com.manish.shop.Cart;
import com.manish.shop.InventoryRepository;
import com.manish.shop.order.Order;
import com.manish.shop.order.OrderService;
import com.manish.shop.order.OrderStatus;
import com.manish.shop.order.OutOfStockException;
import com.manish.shop.payment.FakePaymentGateway;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.manish.qa.support.TestData.FLEECE;
import static org.testng.Assert.assertEquals;

/**
 * STRESS TESTING
 * "What happens BEYOND capacity?" Push far past normal limits and check the system
 * fails gracefully: no crashes, no corrupted data, clear errors for users who miss out.
 * Scenario: a flash sale with 50 jackets and 500 buyers all clicking at the same instant.
 */
@Test(groups = "stress")
public class CheckoutStressTest {

    private static final int STOCK = 50;
    private static final int BUYERS = 500;
    private static final int THREADS = 100;

    public void flashSaleNeverOversells() throws Exception {
        InventoryRepository inventory = new InventoryRepository();
        inventory.addProduct(FLEECE, STOCK);
        OrderService service = TestData.orderService(inventory, new FakePaymentGateway());

        AtomicInteger confirmed = new AtomicInteger();
        AtomicInteger soldOut = new AtomicInteger();
        AtomicInteger unexpectedErrors = new AtomicInteger();

        runAllAtOnce(BUYERS, THREADS, () -> {
            Cart cart = new Cart();
            cart.addItem(FLEECE, 1);
            try {
                service.checkout(cart, null, false, FakePaymentGateway.VALID_TOKEN);
                confirmed.incrementAndGet();
            } catch (OutOfStockException e) {
                soldOut.incrementAndGet();          // graceful, expected failure
            } catch (Exception e) {
                unexpectedErrors.incrementAndGet(); // crash = test failure
            }
        });

        System.out.printf("[stress] confirmed=%d soldOut=%d errors=%d%n", confirmed.get(), soldOut.get(), unexpectedErrors.get());
        assertEquals(confirmed.get(), STOCK, "Exactly the available stock should sell");
        assertEquals(soldOut.get(), BUYERS - STOCK, "Everyone else gets a clean out-of-stock error");
        assertEquals(unexpectedErrors.get(), 0, "No crashes under stress");
        assertEquals(inventory.getStock(FLEECE.sku()), 0, "Stock never goes negative");
    }

    public void mixedDeclinedPaymentsUnderStressKeepStockConsistent() throws Exception {
        InventoryRepository inventory = new InventoryRepository();
        inventory.addProduct(FLEECE, 1_000);
        OrderService service = TestData.orderService(inventory, new FakePaymentGateway());
        AtomicInteger confirmed = new AtomicInteger();
        AtomicInteger counter = new AtomicInteger();

        runAllAtOnce(2_000, THREADS, () -> {
            Cart cart = new Cart();
            cart.addItem(FLEECE, 1);
            // Every third buyer's card is declined, which forces a stock release mid-flight
            String token = counter.incrementAndGet() % 3 == 0 ? FakePaymentGateway.DECLINED_TOKEN : FakePaymentGateway.VALID_TOKEN;
            try {
                Order o = service.checkout(cart, null, false, token);
                if (o.status() == OrderStatus.CONFIRMED) confirmed.incrementAndGet();
            } catch (OutOfStockException ignored) {
                // fine once stock runs out
            }
        });

        assertEquals(inventory.getStock(FLEECE.sku()), 1_000 - confirmed.get(),
                "Every declined payment must have returned its stock");
    }

    /** Starts every task on the same "starting gun" so they collide as hard as possible. */
    private void runAllAtOnce(int tasks, int threads, Runnable task) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch startGun = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < tasks; i++) {
            futures.add(pool.submit(() -> {
                startGun.await();
                task.run();
                return null;
            }));
        }
        startGun.countDown();
        for (Future<?> f : futures) f.get(30, TimeUnit.SECONDS);
        pool.shutdown();
    }
}
