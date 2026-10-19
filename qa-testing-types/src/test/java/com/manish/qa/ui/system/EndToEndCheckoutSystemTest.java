package com.manish.qa.ui.system;

import com.manish.qa.ui.base.BaseUiTest;
import com.manish.qa.ui.pages.CheckoutPage;
import com.manish.qa.ui.pages.InventoryPage;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * SYSTEM TESTING
 * Tests the COMPLETE, deployed application end to end, the way it runs in production:
 * real browser -> real front end -> real back end. Checks the whole system against its
 * functional requirements, done by QA in a test environment after integration testing.
 */
@Test(groups = "system")
public class EndToEndCheckoutSystemTest extends BaseUiTest {

    public void customerCanBuyTwoItemsEndToEnd() {
        InventoryPage inventory = loginAsStandardUser();
        inventory.addToCart("Sauce Labs Backpack")      // $29.99
                 .addToCart("Sauce Labs Bike Light");   // $9.99
        assertEquals(inventory.cartCount(), 2);

        CheckoutPage checkout = inventory.openCart()
                .checkout()
                .enterInformation("Manish", "Tester", "20151");

        assertEquals(checkout.itemTotal(), 39.98, 0.001, "Item total = sum of product prices");

        checkout.finish();
        assertEquals(checkout.confirmationHeader(), "Thank you for your order!");
    }

    public void cartIsEmptyAfterOrderCompletes() {
        InventoryPage inventory = loginAsStandardUser();
        inventory.addToCart("Sauce Labs Onesie");
        inventory.openCart().checkout().enterInformation("Manish", "Tester", "20151").finish();

        driver.get(com.manish.qa.ui.pages.BasePage.BASE_URL + "inventory.html");
        InventoryPage after = new InventoryPage(driver);
        after.waitUntilLoaded();
        assertEquals(after.cartCount(), 0);
    }
}
