package com.manish.qa.performance;

import com.manish.qa.support.TestData;
import com.manish.shop.Cart;
import com.manish.shop.DiscountCalculator;
import com.manish.shop.order.OrderService;
import com.manish.shop.payment.FakePaymentGateway;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static com.manish.qa.support.TestData.*;
import static org.testng.Assert.assertTrue;

/**
 * PERFORMANCE TESTING
 * "How fast is it with normal use?" Measures response time for ONE user against a budget
 * (average and 95th percentile). Load and stress tests then add many users on top.
 * Browser page-load timing is in ui/performance/PageLoadPerformanceUiTest.
 */
@Test(groups = "performance")
public class CheckoutPerformanceTest {

    private static final int WARMUP = 200;
    private static final int SAMPLES = 1_000;
    private static final double AVG_BUDGET_MS = 2.0;
    private static final double P95_BUDGET_MS = 5.0;

    private OrderService service;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        service = TestData.orderService(TestData.stockedInventory(1_000_000), new FakePaymentGateway());
    }

    private Cart sampleCart() {
        Cart cart = new Cart();
        cart.addItem(BACKPACK, 1);
        cart.addItem(BIKE_LIGHT, 2);
        cart.addItem(ONESIE, 1);
        return cart;
    }

    @Test(timeOut = 10_000)
    public void checkoutResponseTimeWithinBudget() {
        for (int i = 0; i < WARMUP; i++) {      // let the JIT compiler warm up first
            service.checkout(sampleCart(), "SAVE10", true, FakePaymentGateway.VALID_TOKEN);
        }

        double[] timesMs = new double[SAMPLES];
        for (int i = 0; i < SAMPLES; i++) {
            Cart cart = sampleCart();
            long start = System.nanoTime();
            service.checkout(cart, "SAVE10", true, FakePaymentGateway.VALID_TOKEN);
            timesMs[i] = (System.nanoTime() - start) / 1_000_000.0;
        }

        Arrays.sort(timesMs);
        double avg = Arrays.stream(timesMs).average().orElse(0);
        double p95 = timesMs[(int) (SAMPLES * 0.95) - 1];
        System.out.printf("[performance] checkout avg=%.3fms p95=%.3fms max=%.3fms%n", avg, p95, timesMs[SAMPLES - 1]);

        assertTrue(avg < AVG_BUDGET_MS, "Average " + avg + "ms exceeds budget " + AVG_BUDGET_MS + "ms");
        assertTrue(p95 < P95_BUDGET_MS, "p95 " + p95 + "ms exceeds budget " + P95_BUDGET_MS + "ms");
    }

    @Test(timeOut = 2_000)
    public void hundredThousandDiscountCalculationsUnderTwoSeconds() {
        DiscountCalculator calc = new DiscountCalculator();
        BigDecimal subtotal = new BigDecimal("75.50");
        for (int i = 0; i < 100_000; i++) {
            calc.calculateDiscount(subtotal, (i % 2 == 0) ? "SAVE10" : "FLAT5", i % 3 == 0);
        }
    }
}
