package com.manish.qa.ui.graybox;

import com.manish.qa.ui.base.BaseUiTest;
import com.manish.qa.ui.pages.InventoryPage;
import com.manish.qa.ui.pages.LoginPage;
import org.openqa.selenium.Cookie;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * GRAY-BOX TESTING
 * Tests through the UI like a user (black box) BUT uses partial knowledge of the internals
 * (white box). Here we know Sauce Demo keeps the cart in the browser's localStorage under
 * "cart-contents" as a list of product ids, and the login in a "session-username" cookie.
 * We use that to (1) verify hidden state and (2) set up state directly to test edge cases faster.
 * Find this kind of knowledge in DevTools > Application tab, API docs, or the database schema.
 */
@Test(groups = "graybox")
public class CartStorageGrayBoxTest extends BaseUiTest {

    private static final String CART_KEY = "cart-contents";
    private static final int BACKPACK_ID = 4;
    private static final int BIKE_LIGHT_ID = 0;

    public void loginCreatesSessionCookie() {
        loginAsStandardUser();
        Cookie session = driver.manage().getCookieNamed("session-username");
        assertNotNull(session, "Login should create a session cookie");
        assertEquals(session.getValue(), STANDARD_USER);
    }

    public void addingToCartIsSavedInLocalStorage() {
        InventoryPage inventory = loginAsStandardUser();
        inventory.addToCart("Sauce Labs Backpack");

        String stored = (String) inventory.runJs("return window.localStorage.getItem(arguments[0]);", CART_KEY);
        assertEquals(stored, "[" + BACKPACK_ID + "]", "UI action should be persisted with the product id");
    }

    public void uiReflectsCartStateInjectedIntoStorage() {
        InventoryPage inventory = loginAsStandardUser();
        // Skip the clicking: write the internal state directly, then check the UI reads it correctly
        inventory.runJs("window.localStorage.setItem(arguments[0], arguments[1]);",
                CART_KEY, "[" + BACKPACK_ID + "," + BIKE_LIGHT_ID + "]");
        driver.navigate().refresh();

        InventoryPage refreshed = new InventoryPage(driver);
        refreshed.waitUntilLoaded();
        assertEquals(refreshed.cartCount(), 2);
        assertTrue(refreshed.openCart().itemNames().contains("Sauce Labs Bike Light"));
    }

    public void removingSessionCookieLogsUserOut() {
        loginAsStandardUser();
        driver.manage().deleteCookieNamed("session-username");
        driver.navigate().refresh();
        // Knowing the cookie IS the session lets us test session expiry without waiting for a timeout
        assertTrue(new LoginPage(driver).errorText().contains("You can only access"),
                "Without the session cookie the user should be sent back to login");
    }
}
