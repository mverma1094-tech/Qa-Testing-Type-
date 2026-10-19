package com.manish.qa.ui.performance;

import com.manish.qa.ui.base.BaseUiTest;
import com.manish.qa.ui.pages.InventoryPage;
import com.manish.qa.ui.pages.LoginPage;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * PERFORMANCE TESTING (browser level)
 * Uses the browser's built-in Navigation Timing API to measure real page-load time,
 * plus a stopwatch around the login action. Budgets are generous because network speed varies.
 * Try changing STANDARD_USER to "performance_glitch_user" to watch these tests catch a slow login.
 */
@Test(groups = "performance-ui")
public class PageLoadPerformanceUiTest extends BaseUiTest {

    private static final double PAGE_LOAD_BUDGET_MS = 5_000;
    private static final long LOGIN_BUDGET_MS = 3_000;

    public void loginPageLoadsWithinBudget() {
        LoginPage login = openLoginPage();
        Number duration = (Number) login.runJs(
                "const nav = performance.getEntriesByType('navigation')[0];" +
                "return nav ? nav.duration : (performance.timing.loadEventEnd - performance.timing.navigationStart);");
        System.out.printf("[performance-ui] login page load = %.0f ms%n", duration.doubleValue());
        assertTrue(duration.doubleValue() < PAGE_LOAD_BUDGET_MS, "Page load " + duration + "ms is over budget");
    }

    public void loginToProductsWithinBudget() {
        LoginPage login = openLoginPage();
        long start = System.currentTimeMillis();

        login.loginAs(STANDARD_USER, PASSWORD);
        new InventoryPage(driver).waitUntilLoaded();

        long elapsed = System.currentTimeMillis() - start;
        System.out.printf("[performance-ui] login -> products = %d ms%n", elapsed);
        assertTrue(elapsed < LOGIN_BUDGET_MS, "Login took " + elapsed + "ms (budget " + LOGIN_BUDGET_MS + "ms)");
    }
}
